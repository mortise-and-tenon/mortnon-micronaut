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
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

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

        return projectRepository.existsByName(createProjectCommand.getName())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("create project fail,project name [{}] is repeat.", createProjectCommand.getName());
                        return Mono.error(RepeatDataException.create("repeat project name."));
                    }
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
    public Mono<Boolean> deleteProject(Long id) {
        if (null == id || id <= 0) {
            return Mono.error(ParameterException.create("project id is not exists."));
        }

        return projectRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("delete project fail,project id [{}] is not exists.", id);
                        return Mono.error(NotFoundException.create("project id is not exists."));
                    }
                    return assignmentRepository.existsByProjectId(id);
                })
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("delete project fail,project id [{}] is used.");
                        return Mono.error(UsedException.create("project is used."));
                    }
                    return projectRepository.deleteById(id);
                })
                .map(result -> result > 0);
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
                        return Mono.error(NotFoundException.create("parent project id is not exists."));
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
                    if (null != updateProjectCommand.getParentId() && updateProjectCommand.getParentId() > 0) {
                        project.setParentId(updateProjectCommand.getParentId());
                    }
                    return projectRepository.update(project);
                })
                .map(SysProjectDTO::convert);
    }
}
