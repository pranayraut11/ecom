package com.ecom.wrapper.database.mongodb.utility;


import com.ecom.shared.contract.dto.PageRequestDTO;
import com.ecom.shared.contract.dto.SearchCriteria;
import com.ecom.shared.contract.enums.Operator;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
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

    public static Query getQuery(PageRequestDTO page) {
        List<Criteria> andCriterias = new ArrayList<>();
        List<Criteria> orCriterias = new ArrayList<>();
        List<Criteria> inCriterias = new ArrayList<>();

        if (Objects.nonNull(page.getAndCriteria())) {
            page.getAndCriteria().forEach(searchCriteria ->
                    andCriterias.add(DBCriteriaUtil.buildCriteria(searchCriteria)));
        }
        if (Objects.nonNull(page.getInCriteria())) {
            page.getInCriteria().forEach(searchCriteria -> {
                if (searchCriteria.getValues() != null && !searchCriteria.getValues().isEmpty()) {
                    inCriterias.add(Criteria.where(searchCriteria.getKey()).in(searchCriteria.getValues()));
                }
            });
        }
        if (Objects.nonNull(page.getOrCriteria())) {
            page.getOrCriteria().forEach(searchCriteria ->
                    orCriterias.add(DBCriteriaUtil.buildCriteria(searchCriteria)));
        }

        Query query = new Query();
        List<Criteria> combinedAnd = new ArrayList<>();
        if (!andCriterias.isEmpty()) combinedAnd.addAll(andCriterias);
        if (!inCriterias.isEmpty()) combinedAnd.addAll(inCriterias);

        Criteria finalAnd = null;
        if (!combinedAnd.isEmpty()) {
            finalAnd = new Criteria().andOperator(combinedAnd.toArray(new Criteria[0]));
        }
        Criteria finalOr = null;
        if (!orCriterias.isEmpty()) {
            finalOr = new Criteria().orOperator(orCriterias.toArray(new Criteria[0]));
        }

        if (finalAnd != null && finalOr != null) {
            query.addCriteria(new Criteria().andOperator(finalAnd).orOperator(finalOr));
        } else if (finalAnd != null) {
            query.addCriteria(finalAnd);
        } else if (finalOr != null) {
            query.addCriteria(finalOr);
        }
        return query;
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
