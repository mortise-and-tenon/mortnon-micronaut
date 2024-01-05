package fun.mortnon.framework.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页数据响应格式
 *
 * @author dev2007
 * @date 2023/2/8
 */
@Serdeable
@Introspected
@Data
public class PageableData<T> {
    /**
     * 当前页数
     */
    @JsonProperty(value = "page_number")
    private Integer pageNumber;
    /**
     * 数据总页数
     */
    @JsonProperty(value = "total_pages")
    private Integer totalPages;
    /**
     * 每页数据量
     */
    @JsonProperty(value = "page_size")
    private Integer pageSize;

    /**
     * 数据总量
     */
    @JsonProperty(value = "total_size")
    private Long totalSize;

    /**
     * 当前页的数据
     */
    private T content;

    public PageableData(int pageNumber, int totalPages, int pageSize, long totalSize, T content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalSize = totalSize;
        this.content = content;
    }
}
