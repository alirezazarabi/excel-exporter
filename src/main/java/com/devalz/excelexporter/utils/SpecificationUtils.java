package com.devalz.excelexporter.utils;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

public class SpecificationUtils {

    public static <T> Optional<Specification<T>> andSpecifications(List<Specification<T>> specificationList) {
        if (CollectionUtils.isEmpty(specificationList)) {
            return Optional.empty();
        }

        Specification<T> specification = where(specificationList.remove(0));
        for (Specification<T> s : specificationList) {
            specification = specification.and(s);
        }
        return Optional.of(specification);
    }
}
