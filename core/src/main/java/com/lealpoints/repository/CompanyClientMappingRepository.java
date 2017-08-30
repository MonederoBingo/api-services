package com.lealpoints.repository;

import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import org.json.JSONObject;
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

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class CompanyClientMappingRepository extends BaseRepository {
    private static final Common c = new Common();
    private final ServiceLocator serviceLocator;
    private final ThreadContextService threadContextService;
    private final ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public CompanyClientMappingRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
    }

    public ServiceResult getByCompanyIdClientId(final long companyId, final long clientId) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId),
                new ColumnValue("client_id", clientId)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("company_client_mapping", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();
    }

    public long insert(CompanyClientMapping companyClientMapping) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("company_id", companyClientMapping.getCompanyId()),
                new ColumnValue("client_id", companyClientMapping.getClient().getClientId())
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("company_client_mapping", values, "company_client_mapping_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());
    }

    public ServiceResult insertIfDoesNotExist(long companyId, long clientId) throws Exception {
        ServiceResult serviceResult = getByCompanyIdClientId(companyId, clientId);
        if ("{}".equals(serviceResult.getObject())) {
            CompanyClientMapping companyClientMapping = new CompanyClientMapping();
            companyClientMapping.setCompanyId(companyId);
            Client client = new Client();
            client.setClientId(clientId);
            companyClientMapping.setClient(client);
            companyClientMapping.setCompanyClientMappingId(insert(companyClientMapping));
            return new ServiceResult(true, "", companyClientMapping.toJSONObject().toString());
        }
        return serviceResult;
    }

    public int updatePoints(CompanyClientMapping companyClientMapping) throws Exception {
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("points", companyClientMapping.getPoints())
        };
        ColumnValue[] filter = new ColumnValue[]{
                new ColumnValue("company_id", companyClientMapping.getCompanyId()),
                new ColumnValue("client_id", companyClientMapping.getClient().getClientId())
        };
        HttpEntity<UpdateQueryRQ> entity = c.getHttpEntityForUpdate(new UpdateQueryRQ("company_client_mapping", values, filter));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/update";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Integer.parseInt(responseEntity.getBody().getObject());
    }
}
