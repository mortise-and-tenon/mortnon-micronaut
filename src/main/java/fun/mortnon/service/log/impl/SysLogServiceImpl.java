package fun.mortnon.service.log.impl;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.repository.LogRepository;
import fun.mortnon.dal.sys.specification.Specifications;
import fun.mortnon.framework.utils.DateTimeUtils;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.web.controller.log.command.LogPageSearch;
import io.micronaut.context.MessageSource;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static io.micronaut.data.repository.jpa.criteria.PredicateSpecification.where;

/**
 * @author dev2007
 * @date 2023/3/7
 */
@Singleton
@Slf4j
public class SysLogServiceImpl implements SysLogService {
    @Inject
    private LogRepository logRepository;

    @Inject
    private MessageSource messageSource;

    @Override
    public Mono<SysLog> createLog(SysLog sysLog) {
        return logRepository.save(sysLog).onErrorResume(e -> {
            log.warn("create operation log fail for:", e);
            return Mono.just(sysLog);
        });
    }

    @Override
    public Mono<Page<SysLogDTO>> queryLogs(LogPageSearch pageSearch, String lang) {
        //默认按时间倒序排列
        Pageable pageable = pageSearch.convert();
        if (!pageable.isSorted()) {
            pageable = pageable.order(Sort.Order.desc("time"));
        }

        PredicateSpecification<SysLog> query = queryCondition(pageSearch);

        return logRepository.findAll(where(query), pageable)
                .map(k -> {
                    List<SysLogDTO> list = k.getContent().stream()
                            .map(log -> SysLogDTO.convert(log, messageSource, lang))
                            .collect(Collectors.toList());
                    return Page.of(list, k.getPageable(), k.getTotalSize());
                });
    }

    private static PredicateSpecification<SysLog> queryCondition(LogPageSearch pageSearch) {
        PredicateSpecification<SysLog> query = null;

        if (StringUtils.isNotEmpty(pageSearch.getIp())) {
            query = Specifications.propertyLike("ip", pageSearch.getIp());
        }
        if (StringUtils.isNotEmpty(pageSearch.getUserName())) {
            PredicateSpecification<SysLog> subQuery = Specifications.propertyLike("userName", pageSearch.getUserName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        if (StringUtils.isNotEmpty(pageSearch.getProjectName())) {
            PredicateSpecification<SysLog> subQuery = Specifications.propertyLike("projectName", pageSearch.getProjectName());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        if (StringUtils.isNotEmpty(pageSearch.getAction())) {
            PredicateSpecification<SysLog> subQuery = Specifications.propertyEqual("action", pageSearch.getAction());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        if (StringUtils.isNotEmpty(pageSearch.getResult())) {
            PredicateSpecification<SysLog> subQuery = Specifications.propertyEqual("result", pageSearch.getResult());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        if (StringUtils.isNotEmpty(pageSearch.getLevel())) {
            PredicateSpecification<SysLog> subQuery = Specifications.propertyEqual("level", pageSearch.getLevel());
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        if (ObjectUtils.isNotEmpty(pageSearch.getBeginTime()) && ObjectUtils.isNotEmpty(pageSearch.getEndTime())) {
            Instant beginInstant = DateTimeUtils.convert(pageSearch.getBeginTime());
            Instant endInstant = DateTimeUtils.convert(pageSearch.getEndTime());
            PredicateSpecification<SysLog> subQuery = Specifications.timeBetween("time", beginInstant, endInstant == Instant.EPOCH ? Instant.MAX : endInstant);
            if (query == null) {
                query = subQuery;
            } else {
                query = query.and(subQuery);
            }
        }
        return query;
    }
}
