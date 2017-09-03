package com.lealpoints.repository;

import org.springframework.http.HttpEntity;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.DeleteQueryRQ;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.UpdateQueryRQ;

class Common {
    private static final ApiClientUtils apiClientUtils = new ApiClientUtils();

    HttpEntity<SelectQueryRQ> getHttpEntityForSelect(SelectQueryRQ selectQueryRQ) {
        return new HttpEntity<>(selectQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<InsertQueryRQ> getHttpEntityForInsert(InsertQueryRQ insertQueryRQ) {
        return new HttpEntity<>(insertQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<UpdateQueryRQ> getHttpEntityForUpdate(UpdateQueryRQ updateQueryRQ) {
        return new HttpEntity<>(updateQueryRQ, apiClientUtils.getHttpHeaders());
    }

    HttpEntity<DeleteQueryRQ> getHttpEntityForDelete(DeleteQueryRQ deleteQueryRQ) {
        return new HttpEntity<>(deleteQueryRQ, apiClientUtils.getHttpHeaders());
    }
}
