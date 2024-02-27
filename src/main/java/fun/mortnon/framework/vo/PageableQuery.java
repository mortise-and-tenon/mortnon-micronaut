package fun.mortnon.framework.vo;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dev2007
 * @date 2024/1/11
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
public class PageableQuery {
    public static final int MAX_PAGE_NUM = 10;
    int page;
    int size;
    String property;
    String order;
    boolean ignoreCase = true;

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

            pageable = Pageable.from(page, size, Sort.of(new Sort.Order(property, direction, ignoreCase)));
        } else {
            pageable = Pageable.from(page, size);
        }

        return pageable;
    }
}
