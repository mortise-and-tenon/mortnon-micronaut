package fun.mortnon.dal.sys.specification;

import io.micronaut.data.repository.jpa.criteria.PredicateSpecification;

/**
 * @author dev2007
 * @date 2024/2/27
 */
public class Specifications {
    public static <T> PredicateSpecification<T> propertyEqual(String key,String value) {
        return (root, criteriaBuilder) -> criteriaBuilder.equal(root.get(key), value);
    }

    public static <T> PredicateSpecification<T> ageIsLessThan(int age) {
        return (root, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("age"), age);
    }
}
