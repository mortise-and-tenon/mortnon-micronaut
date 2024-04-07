package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysAssignment;
import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.repository.RoleRepository;
import fun.mortnon.dal.sys.repository.UserRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UntypedException;
import fun.mortnon.service.sys.PublicService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.ProjectRoleDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdatePasswordCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserStatusCommand;
import fun.mortnon.web.controller.user.command.UserPageSearch;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import io.micronaut.http.multipart.StreamingFileUpload;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;


/**
 * 用户服务
 *
 * @author dongfangzan
 * @date 28.4.21 3:53 下午
 */
@Singleton
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    /**
     * sysUser 仓储
     */
    @Inject
    private UserRepository userRepository;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private PublicService publicService;

    @Override
    @Transactional
    public Mono<SysUserDTO> createUser(CreateUserCommand createUserCommand) {
        if (StringUtils.isEmpty(createUserCommand.getPassword()) || StringUtils.isEmpty(createUserCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("Password is empty"));
        }

        if (!createUserCommand.getPassword().equals(createUserCommand.getRepeatPassword())) {
            return Mono.error(ParameterException.create("Password does not match."));
        }

        if (StringUtils.isEmpty(createUserCommand.getUserName())) {
            return Mono.error(ParameterException.create("Username is empty"));
        }

        if (StringUtils.isEmpty(createUserCommand.getNickName())) {
            return Mono.error(ParameterException.create("Nickname is empty"));
        }

        return userRepository.existsByUserName(createUserCommand.getUserName())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Failed to create user due to duplicate username : {}.", createUserCommand.getUserName());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.USERNAME_ALREADY_EXISTS));
                    }
                    SysUser sysUser = new SysUser();
                    sysUser.setUserName(createUserCommand.getUserName());
                    sysUser.setNickName(createUserCommand.getNickName());
                    sysUser.setPassword(createUserCommand.getPassword());
                    sysUser.setSex(createUserCommand.getSex());

                    boolean status = ObjectUtils.isEmpty(createUserCommand.getStatus()) ? true : createUserCommand.getStatus();
                    sysUser.setStatus(status);

                    sysUser.setEmail(createUserCommand.getEmail());
                    sysUser.setPhone(createUserCommand.getPhone());
                    hashPassword(sysUser);

                    return userRepository.save(sysUser);
                })
                .map(SysUserDTO::convert)
                .flatMap(userDTO -> {
                    if (CollectionUtils.isEmpty(createUserCommand.getProjectRoles())) {
                        return Mono.just(userDTO);
                    }

                    boolean existsEmpty = createUserCommand.getProjectRoles().stream()
                            .anyMatch(k -> null == k.getProjectId() || k.getProjectId() <= 0
                                    || null == k.getRoleId() || k.getRoleId() <= 0);
                    if (existsEmpty) {
                        return Mono.error(ParameterException.create(ErrorCodeEnum.PROJECT_ROLE_ERROR));
                    }
                    return Flux.fromStream(createUserCommand.getProjectRoles().stream())
                            .flatMap(projectRoleDTO -> {
                                return projectRepository.existsById(projectRoleDTO.getProjectId())
                                        .flatMap(projectExists -> {
                                            if (!projectExists) {
                                                return Mono.error(NotFoundException.create(ErrorCodeEnum.PROJECT_NOT_EXIST));
                                            }
                                            return roleRepository.existsById(projectRoleDTO.getRoleId());
                                        })
                                        .flatMap(roleExists -> {
                                            if (!roleExists) {
                                                return Mono.error(NotFoundException.create(ErrorCodeEnum.ROLE_NOT_EXIST));
                                            }
                                            return publicService.saveAssignment(userDTO.getId(), projectRoleDTO.getProjectId(),
                                                    projectRoleDTO.getRoleId());
                                        });
                            })
                            .flatMap(assignment -> queryProjectRole(assignment))
                            .collectList()
                            .map(projectRoles -> {
                                userDTO.setProjectRoles(projectRoles);
                                return userDTO;
                            });
                });
    }

    /**
     * 按关联信息查询相关组织、角色数据
     *
     * @param assignment
     * @return
     */
    private Mono<ProjectRoleDTO> queryProjectRole(SysAssignment assignment) {
        if (ObjectUtils.isNotEmpty(assignment.getProjectId())) {
            return projectRepository.findById(assignment.getProjectId())
                    .map(project -> {
                        ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                        projectRoleDTO.setProjectId(project.getId());
                        projectRoleDTO.setProjectName(project.getName());
                        return projectRoleDTO;
                    })
                    .flatMap(pr -> {
                        if (ObjectUtils.isEmpty(assignment.getRoleId())) {
                            return Mono.just(pr);
                        }
                        return roleRepository.findById(assignment.getRoleId())
                                .map(role -> {
                                    pr.setRoleId(role.getId());
                                    pr.setRoleName(role.getName());
                                    return pr;
                                });
                    });
        }
        return roleRepository.findById(assignment.getRoleId())
                .map(role -> {
                    ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                    projectRoleDTO.setRoleId(role.getId());
                    projectRoleDTO.setRoleName(role.getName());
                    return projectRoleDTO;
                });
    }

    /**
     * Hash 密码，避免明文存储
     *
     * @param sysUser
     */
    private void hashPassword(SysUser sysUser) {
        sysUser.setSalt(RandomStringUtils.randomAlphanumeric(6));
        String hashPassword = DigestUtils.sha256Hex(sysUser.getPassword() + sysUser.getSalt());
        sysUser.setPassword(hashPassword);
    }

    @Override
    public Mono<SysUserDTO> getUserById(Long id) {
        return userRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("Querying user failed, user id {} does not exist.", id);
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }
                    return userRepository.findById(id);
                })
                .flatMap(user -> {
                    SysUserDTO sysUserDTO = SysUserDTO.convert(user);
                    return assignmentRepository.findByUserId(user.getId())
                            .map(assignment -> {
                                ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
                                projectRoleDTO.setRoleId(assignment.getRoleId());
                                projectRoleDTO.setProjectId(assignment.getProjectId());
                                return projectRoleDTO;
                            })
                            .collectList()
                            .map(prList -> {
                                sysUserDTO.setProjectRoles(prList);
                                return sysUserDTO;
                            });
                });
    }

    @Override
    public Mono<Page<SysUserDTO>> queryUsers(UserPageSearch pageSearch) {
        //默认按用户名正序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.asc("userName"));
        }
        return userRepository.findAll(where(queryCondition(pageSearch)), pageable).flatMap(k -> {
            List<SysUserDTO> collect = k.getContent().stream().map(SysUserDTO::convert).collect(Collectors.toList());

            return Flux.fromIterable(collect)
                    .flatMapSequential(userDTO -> {
                        return assignmentRepository.existsByUserId(userDTO.getId())
                                .flatMapMany(exists -> {
                                    if (!exists) {
                                        return Mono.empty();
                                    }

                                    return assignmentRepository.findByUserId(userDTO.getId());
                                })
                                .flatMap(assignment -> queryProjectRole(assignment))
                                .collectList()
                                .map(projectRoles -> {
                                    userDTO.setProjectRoles(projectRoles);
                                    return userDTO;
                                });
                    })
                    .flatMap(userDTO -> {
                        if (ObjectUtils.isNotEmpty(pageSearch.getProjectId())) {
                            if (userDTO.getProjectRoles().size() == 0) {
                                return Mono.empty();
                            }
                            if (!pageSearch.getProjectId().equals(userDTO.getProjectRoles().get(0).getProjectId())) {
                                return Mono.empty();
                            }
                        }
                        return Mono.just(userDTO);
                    })
                    .collectList()
                    .map(list -> Page.of(list, k.getPageable(), k.getTotalSize()));
        });
    }

    /**
     * 组合查询条件
     *
     * @param pageSearch
     * @return
     */
    private PredicateSpecification<SysUser> queryCondition(UserPageSearch pageSearch) {
        PredicateSpecification<SysUser> query = null;

        if (StringUtils.isNotEmpty(pageSearch.getNickName())) {
            query = Specifications.propertyLike("nickName", pageSearch.getNickName());
        }

        if (StringUtils.isNotEmpty(pageSearch.getUserName())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("userName", pageSearch.getUserName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (StringUtils.isNotEmpty(pageSearch.getPhone())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyLike("phone", pageSearch.getPhone());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        if (ObjectUtils.isNotEmpty(pageSearch.getStatus())) {
            PredicateSpecification<SysUser> subQuery = Specifications.propertyEqual("status", pageSearch.getStatus());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }


        return query;
    }


    @Override
    public Mono<SysUser> getUserByUsername(String userName) {
        return userRepository.existsByUserName(userName)
                .flatMap(exists -> {
                    if (exists) {
                        return userRepository.findByUserName(userName);
                    }
                    return Mono.just(new SysUser());
                });
    }

    @Override
    public Mono<Boolean> deleteUser(Long id) {
        if (id == 1) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.DEFAULT_USER_FORBID_DELETE));
        }

        return userRepository.existsById(id)
                .flatMap(result -> {
                    if (!result) {
                        log.warn("Failed to delete user, user id {} does not exist.", id);
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }
                    return assignmentRepository.deleteByUserId(id);
                })
                .flatMap(result -> userRepository.deleteById(id))
                .map(k -> k > 0);
    }

    @Override
    public Mono<Boolean> batchDeleteUser(String ids) {
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
        if (idList.contains(1L)) {
            idList.remove(1L);
        }
        return assignmentRepository.deleteByUserIdInList(idList)
                .flatMap(resultTotal -> userRepository.deleteByIdInList(idList))
                .map(result -> result > 0);
    }

    @Override
    public Mono<SysUserDTO> updateUser(UpdateUserCommand updateUserCommand) {
        return userRepository.existsById(updateUserCommand.getId())
                .flatMap(result -> {
                    if (!result) {
                        log.warn("Failed to update user, user id {} does not exist.", updateUserCommand.getId());
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }
                    return userRepository.findById(updateUserCommand.getId());
                })
                .flatMap(user -> {
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getNickName())).ifPresent(t -> user.setNickName(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getEmail())).ifPresent(t -> user.setEmail(t));
                    Optional.ofNullable(validateUpdate.apply(updateUserCommand.getPhone())).ifPresent(t -> user.setPhone(t));
                    Optional.ofNullable(updateUserCommand.getStatus()).ifPresent(t -> user.setStatus(t));
                    user.setSex(updateUserCommand.getSex());

                    return userRepository.update(user);
                })
                .map(SysUserDTO::convert);
    }

    /**
     * 校验值是否不为空
     */
    private Function<String, String> validateUpdate = data -> {
        if (StringUtils.isNotEmpty(data)) {
            return data;
        }
        return null;
    };

    @Override
    public Flux<SysRole> queryUserRole(String userName) {
        return userRepository.findByUserName(userName)
                .flatMapMany(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(assignment -> roleRepository.findById(assignment.getRoleId()));
    }

    @Override
    public Flux<SysProject> queryUserProject(String userName) {
        return userRepository.findByUserName(userName)
                .flatMapMany(user -> assignmentRepository.findByUserId(user.getId()))
                .flatMap(assignment -> projectRepository.findById(assignment.getProjectId()));
    }

    @Override
    public Mono<Boolean> updateUserPassword(UpdatePasswordCommand updatePasswordCommand) {
        if (null == updatePasswordCommand.getId() || updatePasswordCommand.getId() <= 0) {
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
        }
        if (!updatePasswordCommand.getPassword().equals(updatePasswordCommand.getRepeatPassword())) {
            log.warn("The two entered passwords do not match");
            return Mono.error(ParameterException.create(ErrorCodeEnum.PASSWORD_NOT_MATCH));
        }

        return userRepository.existsById(updatePasswordCommand.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Failed to change user password, user id {} does not exist.", updatePasswordCommand.getId());
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }
                    return userRepository.findById(updatePasswordCommand.getId());
                })
                .flatMap(user -> {
                    user.setPassword(updatePasswordCommand.getPassword());
                    hashPassword(user);
                    return userRepository.update(user);
                })
                .map(result -> result != null);
    }

    @Override
    public Mono<Boolean> updateUserStatus(UpdateUserStatusCommand updateUserStatusCommand) {
        if (ObjectUtils.isEmpty(updateUserStatusCommand.getUserId()) || updateUserStatusCommand.getUserId() <= 0) {
            log.warn("Failed to change user status, user id is empty.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_NOT_EXISTS));
        }

        return userRepository.existsById(updateUserStatusCommand.getUserId())
                .flatMap(exist -> {
                    if (!exist) {
                        log.warn("Failed to change user status, user id {} is not exist.", updateUserStatusCommand.getUserId());
                        return Mono.error(ParameterException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }

                    return userRepository.findById(updateUserStatusCommand.getUserId());
                })
                .flatMap(user -> {
                    user.setStatus(updateUserStatusCommand.isStatus());
                    return userRepository.update(user);
                })
                .map(user -> user != null);
    }

    @Override
    public Mono<Boolean> updateSelfPassword(UpdatePasswordCommand updatePasswordCommand) {
        if (StringUtils.isEmpty(updatePasswordCommand.getUserName())) {
            log.warn("Username is empty.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.USER_INFO_ERROR));
        }

        if (StringUtils.isEmpty(updatePasswordCommand.getOldPassword())) {
            log.warn("Old password is empty.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.OLD_PASSWORD_IS_EMPTY));
        }

        if (!updatePasswordCommand.getPassword().equals(updatePasswordCommand.getRepeatPassword())) {
            log.warn("The two entered passwords do not match.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.PASSWORD_NOT_MATCH));
        }

        return userRepository.existsByUserName(updatePasswordCommand.getUserName())
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Failed to change password, username {} does not exist.", updatePasswordCommand.getUserName());
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.USER_NOT_EXISTS));
                    }
                    return userRepository.findByUserName(updatePasswordCommand.getUserName());
                })
                .flatMap(user -> {
                    user.setPassword(updatePasswordCommand.getPassword());
                    hashPassword(user);
                    return userRepository.update(user);
                })
                .map(result -> result != null);
    }

    @Override
    public Mono<List<SysUserDTO>> importUser(StreamingFileUpload file) {
        try {
            File tempFile = File.createTempFile("user_" + Instant.now().getNano(), "temp");
            return Mono.from(file.transferTo(tempFile))
                    .flatMap(result -> {
                        if (result) {
                            try {
                                List<CreateUserCommand> list = readFile(tempFile);
                                return saveUserList(list);
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        }
                        return Mono.just(new ArrayList<>());
                    });
        } catch (IOException e) {
            log.error("Unable to create temporary file.");
            return Mono.error(UntypedException.create(ErrorCodeEnum.SYSTEM_ERROR));
        }
    }

    private List<CreateUserCommand> readFile(File file) throws Exception {
        List<CreateUserCommand> list = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        int index = 0;

        DataFormatter dataFormatter = new DataFormatter();
        for (Row row : sheet) {
            index++;
            if (index == 1) {
                continue;
            }

            CreateUserCommand createUserCommand = new CreateUserCommand();
            createUserCommand.setProjectRoles(new ArrayList<>());
            ProjectRoleDTO projectRoleDTO = new ProjectRoleDTO();
            createUserCommand.getProjectRoles().add(projectRoleDTO);
            int cellIndex = 0;
            int emptyCellIndex = -1;
            boolean rowEmpty = true;
            for (Cell cell : row) {
                String strValue = dataFormatter.formatCellValue(cell);
                //有单元格数据不为空，当前行不为空行
                if (StringUtils.isNotEmpty(strValue)) {
                    rowEmpty = false;
                } else {
                    //记录必填但为空的单元格
                    if (cellIndex == 0 || cellIndex == 1 || cellIndex == 2 || cellIndex == 3) {
                        emptyCellIndex = cellIndex;
                    }
                }

                switch (cellIndex) {
                    case 0:
                        createUserCommand.setUserName(strValue);
                        break;
                    case 1:
                        projectRoleDTO.setProjectIdentifier(strValue);
                        break;
                    case 2:
                        projectRoleDTO.setRoleIdentifier(strValue);
                        break;
                    case 3:
                        createUserCommand.setNickName(strValue);
                        break;
                    case 4:
                        createUserCommand.setSex(strValue.equals("男") ? 0 : 1);
                        break;
                    case 5:
                        createUserCommand.setPhone(strValue);
                        break;
                    case 6:
                        createUserCommand.setEmail(strValue);
                        break;
                    default:
                        break;
                }
                cellIndex++;
            }

            //如果当前行数据全为空了，不再读取
            if (rowEmpty) {
                break;
            }

            //必填数据为空，提醒
            if (emptyCellIndex != -1) {
                log.warn("Required data is empty,check sheet cell:{}", emptyCellIndex);
                throw ParameterException.create(ErrorCodeEnum.DATA_EMPTY);
            }

            //初始密码：用户名+手机号后4位
            String password = createUserCommand.getUserName() +
                    (StringUtils.isNotEmpty(createUserCommand.getPhone()) ?
                            createUserCommand.getPhone().substring(6) : "");
            createUserCommand.setPassword(password);
            createUserCommand.setRepeatPassword(password);
            list.add(createUserCommand);
        }

        return list;
    }

    private Mono<List<SysUserDTO>> saveUserList(List<CreateUserCommand> createList) {
        return Mono.just(createList)
                .flatMap(list -> {
                    List<String> userNameList = createList.stream().map(CreateUserCommand::getUserName).collect(Collectors.toList());
                    return userRepository.existsByUserNameInList(userNameList)
                            .flatMap(exists -> {
                                if (exists) {
                                    return userRepository.findByUserNameInList(userNameList)
                                            .collectList()
                                            .map(repeatList -> repeatList);
                                }
                                return Mono.just(new ArrayList<SysUser>());
                            });
                })
                .flatMap(repeatList -> {
                    if (CollectionUtils.isNotEmpty(repeatList)) {
                        List<SysUserDTO> list = repeatList.stream().map(SysUserDTO::convert).collect(Collectors.toList());
                        log.warn("Username already exists:{}", list);
                        return Mono.error(RepeatDataException.createWithData(ErrorCodeEnum.USERNAME_ALREADY_EXISTS, list));
                    }

                    return Mono.just(true);
                })
                .flatMapMany(result -> {
                    List<SysUser> userList = new ArrayList<>();
                    createList.forEach(createUserCommand -> {
                        SysUser sysUser = new SysUser();
                        sysUser.setUserName(createUserCommand.getUserName());
                        sysUser.setNickName(createUserCommand.getNickName());
                        sysUser.setPassword(createUserCommand.getPassword());
                        sysUser.setSex(createUserCommand.getSex());

                        sysUser.setStatus(false);

                        sysUser.setEmail(createUserCommand.getEmail());
                        sysUser.setPhone(createUserCommand.getPhone());
                        hashPassword(sysUser);

                        userList.add(sysUser);
                    });
                    return userRepository.saveAll(userList);
                })
                .map(SysUserDTO::convert)
                .flatMap(userDTO -> {
                    CreateUserCommand createUserCommand = createList.stream().filter(create -> create.getUserName().equals(userDTO.getUserName())).findFirst().get();
                    return Flux.fromStream(createUserCommand.getProjectRoles().stream())
                            .flatMap(projectRoleDTO -> {
                                return projectRepository.existsByIdentifier(projectRoleDTO.getProjectIdentifier())
                                        .flatMap(projectExists -> {
                                            if (!projectExists) {
                                                return Mono.error(NotFoundException.create(ErrorCodeEnum.PROJECT_NOT_EXIST));
                                            }
                                            return projectRepository.findByIdentifier(projectRoleDTO.getProjectIdentifier());
                                        })
                                        .flatMap(project -> {
                                            projectRoleDTO.setProjectId(project.getId());
                                            return roleRepository.existsByIdentifier(projectRoleDTO.getRoleIdentifier())
                                                    .flatMap(roleExists -> {
                                                        if (!roleExists) {
                                                            return Mono.error(NotFoundException.create(ErrorCodeEnum.ROLE_NOT_EXIST));
                                                        }
                                                        return roleRepository.findByIdentifier(projectRoleDTO.getRoleIdentifier());
                                                    });
                                        })
                                        .flatMap(role ->
                                                publicService.saveAssignment(userDTO.getId(), projectRoleDTO.getProjectId(),
                                                        role.getId())
                                        );
                            })
                            .flatMap(assignment -> queryProjectRole(assignment))
                            .collectList()
                            .map(projectRoles -> {
                                userDTO.setProjectRoles(projectRoles);
                                return userDTO;
                            });
                }).collectList();

    }
}
