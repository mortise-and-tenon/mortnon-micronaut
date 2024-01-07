package fun.mortnon.service.log.impl;

import fun.mortnon.dal.sys.entity.SysLog;
import fun.mortnon.dal.sys.repository.LogRepository;
import fun.mortnon.service.log.SysLogService;
import fun.mortnon.service.log.vo.SysLogDTO;
import fun.mortnon.web.vo.login.MortnonDefaultPageable;
import io.micronaut.context.MessageSource;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
    public Mono<Page<SysLogDTO>> queryLogs(Pageable pageable, String lang) {
        pageable = pageable.order(Sort.Order.desc("time"));
        return logRepository.findAll(pageable)
                .map(k -> {
                    List<SysLogDTO> list = k.getContent().stream()
                            .map(log -> SysLogDTO.convert(log, messageSource, lang))
                            .collect(Collectors.toList());
                    return Page.of(list, k.getPageable(), k.getTotalSize());
                });
    }
}
