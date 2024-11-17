package com.example.prepics.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.val;

import java.util.function.Supplier;

public class ElasticSearchUtil {

    public static Supplier<Query> createSupplierQuery(String fieldName, String approximate){
        Supplier<Query> supplier = ()->Query.of(q->q.fuzzy(createFuzzyQuery(fieldName, approximate)));
        return  supplier;
    }


    public static FuzzyQuery createFuzzyQuery(String fieldName, String approximate){
        val fuzzyQuery  = new FuzzyQuery.Builder();
        return  fuzzyQuery.field(fieldName).value(approximate).fuzziness("3").build();
    }
}