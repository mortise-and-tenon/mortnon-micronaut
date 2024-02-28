package fun.mortnon.service.log.impl;

import cn.hutool.core.io.FileUtil;
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
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Override
    public Mono<SystemFile> exportFile(LogPageSearch pageSearch, String lang) {
        return queryLogs(pageSearch, lang)
                .map(pageData -> {
                    List<SysLogDTO> contentList = pageData.getContent();
                    Workbook workbook = new HSSFWorkbook();
                    Sheet sheet = workbook.createSheet("操作日志");

                    Row headerRow = sheet.createRow(0);
                    Cell headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("日志编号");
                    headerCell = headerRow.createCell(1);
                    headerCell.setCellValue("用户操作");

                    for (int index = 0; index < contentList.size(); index++) {
                        Row row = sheet.createRow(index + 1);
                        row.createCell(0).setCellValue(contentList.get(index).getId());
                        row.createCell(1).setCellValue(contentList.get(index).getActionDesc());
                    }
                    String fileName = "operlog_" + Instant.now().getEpochSecond() + ".xlsx";
                    File tmpFile = FileUtil.createTempFile(new File("tmp"));
                    try (FileOutputStream outputStream = new FileOutputStream(tmpFile)) {
                        workbook.write(outputStream);
                    } catch (IOException e) {
                        log.warn("write operlog file fail.");
                    }

                    return new SystemFile(tmpFile, MediaType.MICROSOFT_EXCEL_OPEN_XML_TYPE).attach(fileName);
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
