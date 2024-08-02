package com.ecom.shared.common.utility;

import com.ecom.shared.common.dto.PageRequestDTO;
import com.ecom.shared.common.dto.SearchCriteria;
import com.ecom.shared.common.enums.Operator;
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

    public static Query getQuery(PageRequestDTO page){
        List<Criteria> andCriterias = new ArrayList<>();
        List<Criteria> orCriterias = new ArrayList<>();
        if (Objects.nonNull(page.getAndCriteria())) {
            page.getAndCriteria().forEach(searchCriteria ->
                    andCriterias.add(DBCriteriaUtil.buildCriteria(searchCriteria)));
        }
        Criteria andCriteria = new Criteria().andOperator(andCriterias.toArray(new Criteria[andCriterias.size()]));
        if (Objects.nonNull(page.getOrCriteria())) {
            page.getOrCriteria().forEach(searchCriteria ->
                    orCriterias.add(DBCriteriaUtil.buildCriteria(searchCriteria)));
        }
        Criteria orCriteria = new Criteria().orOperator(orCriterias.toArray(new Criteria[orCriterias.size()]));
        Query query = null;
        if (Objects.nonNull(page.getAndCriteria()) && Objects.nonNull(page.getOrCriteria())) {
            query = Query.query(new Criteria().andOperator(andCriteria).orOperator(orCriteria));
        } else if (Objects.nonNull(page.getOrCriteria())) {
            query = Query.query(new Criteria().orOperator(orCriteria));
        } else if (Objects.nonNull(page.getAndCriteria())) {
            query = Query.query(new Criteria().andOperator(andCriteria));
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
