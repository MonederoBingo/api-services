package com.lealpoints.service.implementations;

import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import com.lealpoints.model.PointsInCompany;

import static org.easymock.EasyMock.*;

public class BaseServiceTest {

    public QueryAgent createQueryAgent() throws Exception {
        QueryAgent queryAgent = createMock(QueryAgent.class);
        queryAgent.beginTransaction();
        expectLastCall().times(1);
        queryAgent.commitTransaction();
        expectLastCall().times(1);
        replay(queryAgent);
        return queryAgent;
    }

    public PointsInCompany createCompany(long companyId, String name, String urlImageLogo, float points) {
        PointsInCompany pointsInCompany = new PointsInCompany();
        pointsInCompany.setCompanyId(companyId);
        pointsInCompany.setName(name);
        pointsInCompany.setUrlImageLogo(urlImageLogo);
        pointsInCompany.setPoints(points);
        return pointsInCompany;
    }

    public CompanyClientMapping createClient(float points, String phone) {
        CompanyClientMapping companyClientMapping = new CompanyClientMapping();
        companyClientMapping.setPoints(points);
        Client client = new Client();
        client.setPhone(phone);
        companyClientMapping.setClient(client);
        return companyClientMapping;
    }
}
