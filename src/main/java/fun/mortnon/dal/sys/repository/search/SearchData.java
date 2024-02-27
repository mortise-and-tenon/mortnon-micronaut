package fun.mortnon.dal.sys.repository.search;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/2/27
 */
@Data
@Introspected
@Serdeable
public class SearchData {
    private String column;
    private String value;
}
