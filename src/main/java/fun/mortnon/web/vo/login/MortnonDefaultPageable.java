package fun.mortnon.web.vo.login;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/3/9
 */
@Introspected
@Data
public class MortnonDefaultPageable{
    private final int max;
    private final int number;
    private final Sort sort;

    /**
     * Default constructor.
     *
     * @param page The page
     * @param size The size
     * @param sort The sort
     */
    @Creator
    MortnonDefaultPageable(int page, int size, @Nullable Sort sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index cannot be negative");
        }
        if (size == 0) {
            throw new IllegalArgumentException("Size cannot be 0");
        }
        this.max = size;
        this.number = page;
        this.sort = sort == null ? Sort.unsorted() : sort;
    }
}
