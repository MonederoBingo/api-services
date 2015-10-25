package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Client;
import com.lealpoints.model.CompanyClientMapping;
import org.springframework.stereotype.Component;

@Component
public class CompanyClientMappingRepository extends BaseRepository {

    public CompanyClientMapping getByCompanyIdClientId(final long companyId, final long clientId) throws Exception {
        return getQueryAgent().selectObject(new DbBuilder<CompanyClientMapping>() {
            @Override
            public String sql() {
                return "SELECT * FROM company_client_mapping WHERE company_id = " + companyId + " AND" +
                    " client_id = " + clientId + ";";
            }

            @Override
            public CompanyClientMapping build(ResultSet resultSet) throws SQLException {
                CompanyClientMapping companyClientMapping = new CompanyClientMapping();
                companyClientMapping.setCompanyClientMappingId(resultSet.getLong("company_client_mapping_id"));
                companyClientMapping.setCompanyId(resultSet.getLong("company_id"));
                companyClientMapping.setClient(buildClient(resultSet));
                companyClientMapping.setPoints(resultSet.getFloat("points"));
                return companyClientMapping;
            }

            private Client buildClient(ResultSet resultSet) throws SQLException {
                Client client = new Client();
                client.setClientId(resultSet.getLong("client_id"));
                return client;
            }
        });
    }

    public long insert(CompanyClientMapping companyClientMapping) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO company_client_mapping(company_id, client_id)");
        sql.append(" VALUES (");
        sql.append(companyClientMapping.getCompanyId()).append(", ");
        sql.append(companyClientMapping.getClient().getClientId()).append(");");
        return getQueryAgent().executeInsert(sql.toString(), "company_client_mapping_id");
    }

    public CompanyClientMapping insertIfDoesNotExist(long companyId, long clientId) throws Exception {
        CompanyClientMapping companyClientMapping = getByCompanyIdClientId(companyId, clientId);
        if (companyClientMapping == null) {
            companyClientMapping = new CompanyClientMapping();
            companyClientMapping.setCompanyId(companyId);
            Client client = new Client();
            client.setClientId(clientId);
            companyClientMapping.setClient(client);
            companyClientMapping.setCompanyClientMappingId(insert(companyClientMapping));
        }
        return companyClientMapping;
    }

    public int updatePoints(CompanyClientMapping companyClientMapping) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE company_client_mapping");
        sql.append(" SET points = ").append(companyClientMapping.getPoints());
        sql.append(" WHERE company_id = ").append(companyClientMapping.getCompanyId());
        sql.append(" AND client_id = ").append(companyClientMapping.getClient().getClientId());
        return getQueryAgent().executeUpdate(sql.toString());
    }
}
