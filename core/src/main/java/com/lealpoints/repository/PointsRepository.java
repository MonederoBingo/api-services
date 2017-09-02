package com.lealpoints.repository;

import com.lealpoints.model.Points;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.database.common.ApiClientUtils;
import xyz.greatapp.libs.service.database.requests.InsertQueryRQ;
import xyz.greatapp.libs.service.database.requests.SelectQueryRQ;
import xyz.greatapp.libs.service.database.requests.fields.ColumnValue;
import xyz.greatapp.libs.service.database.requests.fields.Join;
import xyz.greatapp.libs.service.location.ServiceLocator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static xyz.greatapp.libs.service.ServiceName.DATABASE;

@Component
public class PointsRepository extends BaseRepository {

    private static final Common c = new Common();
    private ServiceLocator serviceLocator;
    private ThreadContextService threadContextService;
    private ApiClientUtils apiClientUtils = new ApiClientUtils();

    @Autowired
    public PointsRepository(ServiceLocator serviceLocator, ThreadContextService threadContextService) {
        this.serviceLocator = serviceLocator;
        this.threadContextService = threadContextService;
    }

    public long insert(Points points) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        String date = df.format(points.getDate());
        ColumnValue[] values = new ColumnValue[]{
                new ColumnValue("company_id", points.getCompanyId()),
                new ColumnValue("client_id", points.getClientId()),
                new ColumnValue("sale_key", points.getSaleKey()),
                new ColumnValue("sale_amount", points.getSaleAmount()),
                new ColumnValue("points_to_earn", points.getPointsToEarn()),
                new ColumnValue("required_amount", points.getRequiredAmount()),
                new ColumnValue("earned_points", points.getEarnedPoints()),
                new ColumnValue("date", date)
        };
        HttpEntity<InsertQueryRQ> entity = c.getHttpEntityForInsert(new InsertQueryRQ("points", values, "points_id"));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/insert";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return Long.parseLong(responseEntity.getBody().getObject());

    }

    public ServiceResult getByCompanyIdSaleKey(final long companyId, final String saleKey) throws Exception {
        ColumnValue[] filters = new ColumnValue[]{
                new ColumnValue("company_id", companyId),
                new ColumnValue("sale_key", saleKey)
        };
        HttpEntity<SelectQueryRQ> entity = c.getHttpEntityForSelect(new SelectQueryRQ("points", filters, new Join[0]));
        String url = serviceLocator.getServiceURI(DATABASE, threadContextService.getEnvironment()) + "/select";
        ResponseEntity<ServiceResult> responseEntity = apiClientUtils.getRestTemplate().postForEntity(
                url,
                entity,
                ServiceResult.class);
        return responseEntity.getBody();

    }
}
