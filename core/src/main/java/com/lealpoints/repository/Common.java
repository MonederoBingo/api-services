package com.lealpoints.repository;

import org.springframework.http.HttpEntity;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;

class Common {
    private static final ApiClientUtils apiClientUtils = new ApiClientUtils();

    HttpEntity<SelectQueryRQ> getHttpEntityForSelect(SelectQueryRQ selectQueryRQ) {
        return new HttpEntity<>(selectQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<InsertQueryRQ> getHttpEntityForInsert(InsertQueryRQ insertQueryRQ) {
        return new HttpEntity<>(insertQueryRQ, apiClientUtils.getHttpHeaders());
    }
}
