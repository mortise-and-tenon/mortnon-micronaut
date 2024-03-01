package fun.mortnon.dal.sys.specification;

import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;

import java.time.Instant;

/**
 * 搜索用查询条件
 *
 * @author dev2007
 * @date 2024/2/27
 */
public class Specifications {

    /**
     * 字段数据一致的查询约束
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public static <T> PredicateSpecification<T> propertyEqual(String key, String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    public static <T> PredicateSpecification<T> propertyEqual(String key, boolean value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    /**
     * 字段数据包含的查询约束
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public static <T> PredicateSpecification<T> propertyLike(String key, String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.like(root.get(key), "%" + value + "%");
    }

    /**
     * 字段日期数据范围内查询约束
     * @param key
     * @param beginTime
     * @param endTime
     * @return
     * @param <T>
     */
    public static <T> PredicateSpecification<T> timeBetween(String key, Instant beginTime, Instant endTime) {
        return (root, criteriaBuilder) -> criteriaBuilder.between(root.get(key), beginTime, endTime);
    }
}
