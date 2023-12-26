package com.ecom.product.utility;

import com.ecom.product.dto.Operator;
import com.ecom.product.dto.SearchCriteria;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DBCriteriaUtil {

    public static final Map<Operator, Function<SearchCriteria,Criteria>> FILTER_CRITERIA= new HashMap<>();

    static {
        FILTER_CRITERIA.put(Operator.EQUAL, condition -> Criteria.where(condition.getKey()).is(condition.getValue()));
        FILTER_CRITERIA.put(Operator.NOT_EQUAL, condition -> Criteria.where(condition.getKey()).ne(condition.getValue()));
        FILTER_CRITERIA.put(Operator.GREATER_THAN, condition -> Criteria.where(condition.getKey()).gt(condition.getValue()));
        FILTER_CRITERIA.put(Operator.GREATER_THAN_OR_EQUAL_TO, condition -> Criteria.where(condition.getKey()).gte(condition.getValue()));
        FILTER_CRITERIA.put(Operator.LESS_THAN, condition -> Criteria.where(condition.getKey()).lt(condition.getValue()));
        FILTER_CRITERIA.put(Operator.LESSTHAN_OR_EQUAL_TO, condition -> Criteria.where(condition.getKey()).lte(condition.getValue()));
        FILTER_CRITERIA.put(Operator.CONTAINS, condition -> Criteria.where(condition.getKey()).regex((String) condition.getValue()));
        FILTER_CRITERIA.put(Operator.JOIN, condition ->  Criteria.where(condition.getKey()).is(condition.getValue()));

    }

    public static Criteria buildCriteria(SearchCriteria condition) {
        Function<SearchCriteria, Criteria>
                function = FILTER_CRITERIA.get(condition.getOperator());

        if (function == null) {
            throw new IllegalArgumentException("Invalid function param type: ");
        }

        return function.apply(condition);
    }
}
