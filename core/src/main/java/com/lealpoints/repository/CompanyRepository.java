package com.lealpoints.repository;

import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Company;
import com.lealpoints.model.PointsInCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.UpdateQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.database.requests.fields.Join;
import xyz.greatapp.libs.service.location.ServiceLocator;

import java.sql.ResultSet;
import java.sql.SQLException;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class CompanyRepository extends BaseRepository {
    private static final Common c = new Common();
    private final ServiceLocator serviceLocator;
    private final ThreadContextService threadContextService;
    private final ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public CompanyRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
    }

    public long insert(Company company) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("name", company.getName()),
                new ColumnValue("url_image_logo", company.getUrlImageLogo())
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("company", values, "company_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());
    }

    public int updateUrlImageLogo(long companyId, String urlImageLogo) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("url_image_logo", urlImageLogo)
        };
        ColumnValue[] filter = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };
        HttpEntity<UpdateQueryRQ> entity = c.getHttpEntityForUpdate(new UpdateQueryRQ("company", values, filter));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Integer.parseInt(responseEntity.getBody().getObject());
    }

    public ServiceResult getByCompanyId(final long companyId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("company", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }

    public ServiceResult getPointsInCompanyByClientId(final long clientId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("client_id", clientId, "company_client_mapping")
        };
        Join[] joins = new Join[]{
                new Join("company_client_mapping", "company_id", "company_id")
        };

        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("company", filters, joins));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/selectList";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }

}
