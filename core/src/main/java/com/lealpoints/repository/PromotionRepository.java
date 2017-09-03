package com.lealpoints.repository;

import com.lealpoints.model.Promotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.location.ServiceLocator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class PromotionRepository {
    private static final Common c = new Common();
    private ServiceLocator serviceLocator;
    private ThreadContextService threadContextService;
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public PromotionRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
    }

    public long insert(Promotion promotion) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        String date = df.format(promotion.getDate());
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("company_id", promotion.getCompanyId()),
                new ColumnValue("client_id", promotion.getClientId()),
                new ColumnValue("description", promotion.getDescription()),
                new ColumnValue("used_points", promotion.getUsedPoints()),
                new ColumnValue("date", date)
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("promotion", values, "promotion_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());

    }
}
