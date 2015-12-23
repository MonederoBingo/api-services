package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.lealpoints.db.util.DbBuilder;
import com.lealpoints.model.Company;
import com.lealpoints.model.PointsInCompany;
import org.springframework.stereotype.Component;

@Component
public class CompanyRepository extends BaseRepository {

    public long insert(Company company) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO company (name, url_image_logo)");
        sql.append(" VALUES (");
        sql.append("'").append(company.getName()).append("', ");
        sql.append("'").append(company.getUrlImageLogo()).append("');");

        return getQueryAgent().executeInsert(sql.toString(), "company_id");
    }

    public int updateUrlImageLogo(long companyId, String urlImageLogo) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE company");
        sql.append(" SET url_image_logo = '").append(urlImageLogo).append("'");
        sql.append(" WHERE company_id = ").append(companyId).append(";");
        return getQueryAgent().executeUpdate(sql.toString());
    }

    public Company getByCompanyId(final long companyId) throws Exception {
        final Company company = getQueryAgent().selectObject(new DbBuilder<Company>() {
            @Override
            public String sql() {
                return "SELECT * FROM company WHERE company_id = ?;";
            }

            @Override
            public Object[] values() {
                return new Object[]{companyId};
            }

            @Override
            public Company build(ResultSet resultSet) throws SQLException {
                Company company = new Company();
                company.setCompanyId(resultSet.getLong("company_id"));
                company.setName(resultSet.getString("name"));
                company.setUrlImageLogo(resultSet.getString("url_image_logo"));
                return company;
            }
        });
        return company;
    }

    public List<PointsInCompany> getPointsInCompanyByClientId(final long clientId) throws Exception {
        return getQueryAgent().selectList(new DbBuilder<PointsInCompany>() {
            @Override
            public String sql() {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT company.*, company_client_mapping.points FROM ").append("company");
                sql.append(" INNER JOIN company_client_mapping USING (").append("company_id").append(")");
                sql.append(" WHERE company_client_mapping.client_id = ? ;");
                return sql.toString();
            }

            @Override
            public Object[] values() {
                return new Object[]{clientId};
            }

            @Override
            public PointsInCompany build(ResultSet resultSet) throws SQLException {
                PointsInCompany pointsInCompany = new PointsInCompany();
                pointsInCompany.setCompanyId(resultSet.getLong("company_id"));
                pointsInCompany.setName(resultSet.getString("name"));
                pointsInCompany.setUrlImageLogo(resultSet.getString("url_image_logo"));
                pointsInCompany.setPoints(resultSet.getFloat("points"));
                return pointsInCompany;
            }
        });
    }

}
