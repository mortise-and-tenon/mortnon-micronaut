package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysProject;
import fun.mortnon.dal.sys.repository.AssignmentRepository;
import fun.mortnon.dal.sys.repository.ProjectRepository;
import fun.mortnon.framework.exceptions.NotFoundException;
import fun.mortnon.framework.exceptions.ParameterException;
import fun.mortnon.framework.exceptions.RepeatDataException;
import fun.mortnon.framework.exceptions.UsedException;
import fun.mortnon.service.sys.SysProjectService;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        sysProject.setStatus(createProjectCommand.isStatus());

        return projectRepository.existsByName(createProjectCommand.getName())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("create project fail,project name [{}] is repeat.", createProjectCommand.getName());
                        return Mono.error(RepeatDataException.create("result.project.name.repeat.fail"));
                    }

                    return projectRepository.existsById(createProjectCommand.getParentId())
                            .flatMap(parentExists -> {
                                if (!parentExists) {
                                    return Mono.error(NotFoundException.create("result.project.id.invalid.fail"));
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
    public Mono<Page<SysProjectDTO>> queryProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(k -> {
                    List<SysProjectDTO> collect = k.getContent().stream().map(SysProjectDTO::convert).collect(Collectors.toList());
                    return Page.of(collect, k.getPageable(), k.getTotalSize());
                });
    }

    @Override
    public Mono<SysProjectTreeDTO> queryTreeProjects() {
        return projectRepository.findAll()
                .collectList()
                .map(list -> {
                    SysProject root = list.stream().filter(data -> data.getParentId() == 0L).findAny().orElse(null);
                    if (null == root) {
                        log.warn("root project is empty.");
                        return new SysProjectTreeDTO();
                    }
                    SysProjectTreeDTO rootNode = SysProjectTreeDTO.convert(root);
                    return createTree(rootNode, list);
                });
    }

    private SysProjectTreeDTO createTree(SysProjectTreeDTO parentNode, List<SysProject> list) {

        List<SysProject> childrenList = list.stream().filter(data -> data.getParentId().equals(parentNode.getId())).collect(Collectors.toList());
        List<SysProjectTreeDTO> childrenNode = childrenList.stream().map(SysProjectTreeDTO::convert).collect(Collectors.toList());
        parentNode.setChildren(childrenNode);

        childrenNode.forEach(child -> createTree(child, list));

        return parentNode;
    }

    @Override
    public Mono<Boolean> deleteProject(Long id) {
        if (null == id || id <= 0) {
            return Mono.error(ParameterException.create("result.project.id.invalid.fail"));
        }

        if (id == 1) {
            return Mono.error(ParameterException.create("result.data.default.delete.fail"));
        }

        return projectRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("delete project fail,project id [{}] is not exists.", id);
                        return Mono.error(NotFoundException.create("result.project.id.invalid.fail"));
                    }
                    return assignmentRepository.existsByProjectId(id);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("delete project fail,project id [{}] is used.");
                        return Mono.error(UsedException.create("result.project.delete.used.fail"));
                    }

                    return projectRepository.existsById(id)
                            .flatMap(projectExists -> {
                                if (!projectExists) {
                                    log.info("project {}  is deleted.", id);
                                    return Mono.just(new ArrayList<Long>());
                                }
                                return projectRepository.findAll()
                                        .flatMap(project -> {
                                            List<Long> idList = Arrays.stream(project.getAncestors().split(","))
                                                    .map(Long::parseLong)
                                                    .collect(Collectors.toList());
                                            //如果是子组织，要进行判断。已使用，当前组织也不可删除
                                            if (idList.contains(id)) {
                                                return assignmentRepository.existsByProjectId(project.getId())
                                                        .map(used -> used ? -1L : project.getId());
                                            }

                                            return Mono.just(0L);
                                        }).collectList();
                            });
                })
                .flatMap(list -> {
                    list = list.stream().filter(value -> value != 0L).collect(Collectors.toList());
                    //已不存在的组织数据，直接响应成功
                    if (list.size() == 0) {
                        return Mono.just(true);
                    }
                    //有子组织被使用，不能删除
                    if (list.contains(-1L)) {
                        return Mono.error(UsedException.create("result.project.child.delete.used.fail"));
                    }
                    list.add(id);
                    return projectRepository.deleteByIdIn(list).map(size -> size > 0);
                });
    }

    @Override
    public Mono<SysProjectDTO> updateProject(UpdateProjectCommand updateProjectCommand) {
        return projectRepository.existsByNameEqualsAndIdNotEquals(updateProjectCommand.getName(), updateProjectCommand.getId())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("update project fail,project id [{}],name [{}] is repeat.", updateProjectCommand.getId(),
                                updateProjectCommand.getName());
                        return Mono.error(RepeatDataException.create("project name is repeat."));
                    }
                    if (null != updateProjectCommand.getParentId() && updateProjectCommand.getParentId() > 0) {
                        return projectRepository.existsById(updateProjectCommand.getParentId());
                    }
                    return Mono.just(true);
                })
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("create project fail,parent id [{}] is not exists.");
                        return Mono.error(NotFoundException.create("result.project.parent.not.exists"));
                    }

                    return projectRepository.findById(updateProjectCommand.getId());
                })
                .flatMap(project -> {
                    if (StringUtils.isNotEmpty(updateProjectCommand.getName())) {
                        project.setName(updateProjectCommand.getName());
                    }
                    if (StringUtils.isNotEmpty(updateProjectCommand.getDescription())) {
                        project.setDescription(updateProjectCommand.getDescription());
                    }
                    if (updateProjectCommand.getOrder() > 0) {
                        project.setOrder(updateProjectCommand.getOrder());
                    }

                    project.setStatus(updateProjectCommand.isStatus());

                    if (null != updateProjectCommand.getParentId() && updateProjectCommand.getParentId() > 0) {
                        project.setParentId(updateProjectCommand.getParentId());
                        return projectRepository.findById(updateProjectCommand.getParentId())
                                .map(parentNode -> {
                                    project.setAncestors(parentNode.getAncestors() + "," + updateProjectCommand.getParentId());
                                    return project;
                                });
                    }

                    return Mono.just(project);
                })
                .flatMap(project -> projectRepository.update(project))
                .map(SysProjectDTO::convert);
    }
}
