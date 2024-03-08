package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysProjectService;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.ProjectPageSearch;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Singleton
@Slf4j
public class SysProjectServiceImpl implements SysProjectService {
    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private AssignmentRepository assignmentRepository;

    @Override
    public Mono<SysProjectDTO> createProject(CreateProjectCommand createProjectCommand) {
        SysProject sysProject = new SysProject();
        sysProject.setName(createProjectCommand.getName());
        sysProject.setDescription(createProjectCommand.getDescription());
        sysProject.setParentId(createProjectCommand.getParentId());
        sysProject.setOrder(createProjectCommand.getOrder() > 0 ? createProjectCommand.getOrder() : 1);
        boolean status = ObjectUtils.isNotEmpty(createProjectCommand.getStatus()) ? createProjectCommand.getStatus() : true;
        sysProject.setStatus(status);

        return projectRepository.existsByName(createProjectCommand.getName())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Failed to create organization, organization name {} duplicates.", createProjectCommand.getName());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.PROJECT_REPEAT));
                    }

                    return projectRepository.existsById(createProjectCommand.getParentId())
                            .flatMap(parentExists -> {
                                if (!parentExists) {
                                    return Mono.error(NotFoundException.create(ErrorCodeEnum.PROJECT_ERROR));
                                }
                                return projectRepository.findById(createProjectCommand.getParentId());
                            });
                })
                .flatMap(parentNode -> {
                    sysProject.setAncestors(StringUtils.isEmpty(parentNode.getAncestors()) ? "" + parentNode.getId() :
                            parentNode.getAncestors() + "," + parentNode.getId());
                    return projectRepository.save(sysProject).map(SysProjectDTO::convert);
                });

    }

    @Override
    public Mono<Page<SysProjectDTO>> queryProjects(ProjectPageSearch pageSearch) {
        //默认按id正序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.asc("id"));
        }

        return projectRepository.findAll(where(queryCondition(pageSearch)), pageable)
                .map(k -> {
                    List<SysProjectDTO> content = k.getContent().stream().map(SysProjectDTO::convert).collect(Collectors.toList());
                    return Page.of(content, k.getPageable(), k.getTotalSize());
                });
    }

    /**
     * 组织查询条件
     *
     * @param search
     * @return
     */
    private PredicateSpecification<SysProject> queryCondition(ProjectPageSearch search) {
        PredicateSpecification<SysProject> query = null;

        if (StringUtils.isNotEmpty(search.getName())) {
            query = Specifications.propertyLike("name", search.getName());
        }

        if (ObjectUtils.isNotEmpty(search.getStatus())) {
            PredicateSpecification<SysProject> subQuery = Specifications.propertyEqual("status", search.getStatus());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }

        return query;
    }

    @Override
    public Mono<SysProjectDTO> queryProjectById(Long id) {
        return projectRepository.findById(id).map(SysProjectDTO::convert);
    }

    @Override
    public Mono<List<SysProjectTreeDTO>> queryTreeProjects(ProjectPageSearch pageSearch) {
        return projectRepository.findAll(where(queryCondition(pageSearch)))
                .collectList()
                .map(list -> {
                    List<SysProjectTreeDTO> tree = new ArrayList<>();
                    list.forEach(node -> {
                        boolean result = bindToParent(tree, node);
                        if (!result) {
                            tree.add(SysProjectTreeDTO.convert(node));
                        }
                    });
                    return tree;
                });
    }

    private boolean bindToParent(List<SysProjectTreeDTO> list, SysProject node) {
        for (SysProjectTreeDTO current : list) {
            if (current.getId() == node.getParentId()) {
                current.getChildren().add(SysProjectTreeDTO.convert(node));
                return true;
            }
            if (current.getChildren().size() > 0) {
                return bindToParent(current.getChildren(), node);
            }
        }
        return false;
    }

    @Override
    public Mono<Boolean> deleteProject(Long id) {
        if (null == id || id <= 0) {
            log.warn("Failed to delete department, department id incorrect.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.PROJECT_ERROR));
        }

        if (id == 1) {
            log.warn("Department deletion failed, default department cannot be deleted.");
            return Mono.error(ParameterException.create(ErrorCodeEnum.DEFAULT_PROJECT_FORBID_DELETE));
        }

        return projectRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Department deletion failed, department id {} does not exist.", id);
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.PROJECT_ERROR));
                    }
                    return assignmentRepository.existsByProjectId(id);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Department deletion failed, department {} has been used.", id);
                        return Mono.error(UsedException.create(ErrorCodeEnum.PROJECT_USED));
                    }

                    return projectRepository.findAll()
                            .flatMap(project -> {
                                List<Long> idList = Arrays.stream(project.getAncestors().split(","))
                                        .map(Long::parseLong)
                                        .collect(Collectors.toList());
                                //如果是子组织，要进行判断。子组织已使用，当前组织也不可删除
                                if (idList.contains(id)) {
                                    return assignmentRepository.existsByProjectId(project.getId())
                                            .map(used -> used ? -1L : project.getId());
                                }

                                return Mono.just(0L);
                            }).collectList();

                })
                .flatMap(list -> {
                    list = list.stream().filter(value -> value != 0L).collect(Collectors.toList());
                    //已不存在的组织数据，直接响应成功
                    if (list.size() == 0) {
                        return Mono.just(true);
                    }
                    //有子组织被使用，不能删除
                    if (list.contains(-1L)) {
                        log.warn("Sub-organization is in use, unable to delete organization {}.", id);
                        return Mono.error(UsedException.create(ErrorCodeEnum.CHILD_PROJECT_USED));
                    }
                    list.add(id);
                    return projectRepository.deleteByIdIn(list).map(size -> size > 0);
                });
    }

    @Override
    public Mono<SysProjectDTO> updateProject(UpdateProjectCommand updateProjectCommand) {
        //查找是否会重名
        return projectRepository.existsByNameEqualsAndIdNotEquals(updateProjectCommand.getName(), updateProjectCommand.getId())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Modification of the department failed, the department id [{}] has the same name [{}].", updateProjectCommand.getId(),
                                updateProjectCommand.getName());
                        return Mono.error(RepeatDataException.create(ErrorCodeEnum.PROJECT_REPEAT));
                    }
                    //判断父组织id是否正确
                    if (null != updateProjectCommand.getParentId() && updateProjectCommand.getParentId() > 0) {
                        return projectRepository.existsById(updateProjectCommand.getParentId());
                    }
                    return Mono.just(true);
                })
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Failed to create department, parent department id {} does not exist.", updateProjectCommand.getParentId());
                        return Mono.error(NotFoundException.create(ErrorCodeEnum.PARENT_PROJECT_NOT_EXIST));
                    }

                    //判断组织id存在，查找对应数据
                    return projectRepository.existsById(updateProjectCommand.getId())
                            .flatMap(projectExists -> {
                                if (!projectExists) {
                                    log.warn("Failed to create department, department id {} does not exist.", updateProjectCommand.getId());
                                    return Mono.error(NotFoundException.create(ErrorCodeEnum.PROJECT_NOT_EXIST));
                                }
                                return projectRepository.findById(updateProjectCommand.getId());
                            });
                })
                .flatMap(project -> {
                    //更新组织数据
                    if (StringUtils.isNotEmpty(updateProjectCommand.getName())) {
                        project.setName(updateProjectCommand.getName());
                    }
                    if (StringUtils.isNotEmpty(updateProjectCommand.getDescription())) {
                        project.setDescription(updateProjectCommand.getDescription());
                    }
                    if (updateProjectCommand.getOrder() > 0) {
                        project.setOrder(updateProjectCommand.getOrder());
                    }

                    boolean status = ObjectUtils.isNotEmpty(updateProjectCommand.getStatus()) ? updateProjectCommand.getStatus() : true;
                    project.setStatus(status);

                    if (null != updateProjectCommand.getParentId() && updateProjectCommand.getParentId() > 0) {
                        project.setParentId(updateProjectCommand.getParentId());
                        //拼接所有父组织id串
                        return projectRepository.findById(updateProjectCommand.getParentId())
                                .map(parentNode -> {
                                    String ancestors = parentNode.getAncestors() +
                                            (StringUtils.isNotEmpty(parentNode.getAncestors()) ? "," : "") + updateProjectCommand.getParentId();

                                    project.setAncestors(ancestors);
                                    return project;
                                });
                    }

                    return Mono.just(project);
                })
                .flatMap(project -> projectRepository.update(project))
                .map(SysProjectDTO::convert);
    }
}
