package fun.mortnon.dal.sys.specification;

import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;

import java.time.Instant;
import java.util.List;

/**
 * 搜索用查询条件
 *
 * @author dev2007
 * @date 2024/2/27
 */
public class Specifications {

    /**
     * 字段数据一致的查询约束
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public static <T> PredicateSpecification<T> propertyEqual(String key, String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    public static <T> PredicateSpecification<T> propertyEqual(String key, boolean value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    public static <T> PredicateSpecification<T> propertyEqual(String key, int value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    /**
     * 字段数据包含的查询约束
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public static <T> PredicateSpecification<T> propertyLike(String key, String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.like(root.get(key), "%" + value + "%");
    }

    /**
     * 字段日期数据范围内查询约束
     *
     * @param key
     * @param beginTime
     * @param endTime
     * @param <T>
     * @return
     */
    public static <T> PredicateSpecification<T> timeBetween(String key, Instant beginTime, Instant endTime) {
        return (root, criteriaBuilder) -> criteriaBuilder.between(root.get(key), beginTime, endTime);
    }

    public static <T> PredicateSpecification<T> idInList(String key, List<Long> list) {
        return (root, criteriaBuilder) -> root.get(key).in(list);
    }

    public static <T> PredicateSpecification<T> idNotInList(String key, List<Long> list) {
        return (root, criteriaBuilder) -> root.get(key).in(list).not();
    }
}
