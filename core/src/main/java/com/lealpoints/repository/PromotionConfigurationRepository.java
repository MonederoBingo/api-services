package com.lealpoints.repository;

import com.lealpoints.model.PromotionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.DeleteQueryRQ;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.UpdateQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.database.requests.fields.Join;
import xyz.greatapp.libs.service.location.ServiceLocator;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class PromotionConfigurationRepository {

    private static final Common c = new Common();
    private ServiceLocator serviceLocator;
    private ThreadContextService threadContextService;
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public PromotionConfigurationRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
    }

    public ServiceResult getById(final long id) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("promotion_configuration_id", id)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("promotion_configuration", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }

    public ServiceResult getByCompanyId(final long companyId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("promotion_configuration",
                filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);

        return responseEntity.getBody();

    }

    public long insert(PromotionConfiguration promotionConfiguration) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("company_id", promotionConfiguration.getCompanyId()),
                new ColumnValue("description", promotionConfiguration.getDescription()),
                new ColumnValue("required_points", promotionConfiguration.getRequiredPoints())
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("promotion_configuration",
                values, "promotion_configuration_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());

    }

    public int delete(long promotionConfigurationId) throws Exception {
        ColumnValue[] filter = new ColumnValue[]{
                new ColumnValue("promotion_configuration_id", promotionConfigurationId)
        };
        HttpEntity<DeleteQueryRQ> entity = c.getHttpEntityForDelete(new DeleteQueryRQ("promotion_configuration", filter));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/delete";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Integer.parseInt(responseEntity.getBody().getObject());

    }
}
