package fun.mortnon.framework.vo;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

/**
 * 分页、排序参数
 * page=0&size=10&property=time&order=asc
 * page: 当前页数，从0开始
 * size：每页条数
 * property：排序字段
 * order：排序方式，asc 正序，desc 倒序
 * @author dev2007
 * @date 2024/1/11
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class PageableQuery {
    public static final int MAX_PAGE_NUM = 10;
    private int page;
    private int size;
    private String property;
    private String order;
    private boolean ignoreCase = true;

    public Pageable convert() {

        if (size == 0) {
            size = MAX_PAGE_NUM;
        }

        Sort.Order.Direction direction = Sort.Order.Direction.DESC;

        Pageable pageable;

        if (StringUtils.isNotEmpty(property)) {
            if (StringUtils.isNotEmpty(order)) {
                if (order.equalsIgnoreCase("asc")) {
                    direction = Sort.Order.Direction.ASC;
                }
            }

            pageable = Pageable.from(page, size, Sort.of(new Sort.Order(CaseUtils.toCamelCase(property,false,new char[]{'_'}), direction, ignoreCase)));
        } else {
            pageable = Pageable.from(page, size);
        }

        return pageable;
    }
}
