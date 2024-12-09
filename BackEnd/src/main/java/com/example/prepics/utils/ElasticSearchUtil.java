package com.example.prepics.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co. elastic. clients. elasticsearch._types. query_dsl. Operator;

import java.util.function.Supplier;

public class ElasticSearchUtil {

    public static Supplier<Query> createSupplierQuery(String fieldName, String approximate){
        return ()->Query.of(q->q.match(createFuzzyQuery(fieldName, approximate)));
    }

    public static MatchQuery createFuzzyQuery(String fieldName, String approximate){
        return QueryBuilders.match().field(fieldName).query(approximate)
                .operator(Operator.And).fuzziness("auto").build();
    }
}