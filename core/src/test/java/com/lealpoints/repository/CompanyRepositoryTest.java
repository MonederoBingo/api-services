package com.lealpoints.repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import com.lealpoints.db.queryagent.QueryAgent;
import com.lealpoints.model.Company;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.repository.fixtures.CompanyRepositoryFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompanyRepositoryTest extends BaseRepositoryTest {

    private CompanyRepository _companyRepository;
    private CompanyRepositoryFixture _companyFixture = new CompanyRepositoryFixture();

    @Before
    public void setUp() throws Exception {
        try {
            _companyRepository = createCompanyRepository(getQueryAgent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() throws Exception {
        Company expectedCompany = new Company();
        expectedCompany.setCompanyId(1);
        expectedCompany.setName("gasolinera");
        expectedCompany.setUrlImageLogo("logo");
        final long companyId = _companyRepository.insert(expectedCompany);
        Company actualCompany = getCompanyById(companyId);
        Assert.assertEquals(companyId, actualCompany.getCompanyId());
        Assert.assertEquals(expectedCompany.getName(), actualCompany.getName());
        Assert.assertEquals(expectedCompany.getUrlImageLogo(), actualCompany.getUrlImageLogo());
    }

    @Test
    public void testGetPointsInCompanyByClientIdId() throws Exception {
        executeFixture(_companyFixture.insertClientTwoCompaniesAndMapping());
        List<PointsInCompany> companies = _companyRepository.getPointsInCompanyByClientId(1);
        assertNotNull(companies);
        assertEquals(2, companies.size());
    }

    @Test
    public void testUpdateUrlImageLogo() throws Exception {
        executeFixture(_companyFixture.insertCompany());
        Company company = getCompanyById(1);
        assertNotNull(company);
        assertEquals("B", company.getUrlImageLogo());
        _companyRepository.updateUrlImageLogo(1, "logo.png");
        company = getCompanyById(1);
        assertNotNull(company);
        assertEquals("logo.png", company.getUrlImageLogo());
    }

    @Test
    public void testGetByCompanyId() throws Exception {
        executeFixture(_companyFixture.insertCompany());
        Company company = _companyRepository.getByCompanyId(1);
        assertNotNull(company);
        assertEquals("A", company.getName());
        assertEquals("B", company.getUrlImageLogo());
    }

    private Company getCompanyById(long companyId) throws Exception {
        Statement st = null;
        Company company = null;
        try {
            st = getQueryAgent().getConnection().createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM company WHERE company_id = " + companyId);
            if(resultSet.next()) {
                company = new Company();
                company.setCompanyId(resultSet.getLong("company_id"));
                company.setName(resultSet.getString("name"));
                company.setUrlImageLogo(resultSet.getString("url_image_logo"));
            }
        }
        finally {
            if (st != null) {
                st.close();
            }
        }
        return company;
    }

    private CompanyRepository createCompanyRepository(final QueryAgent queryAgent) throws Exception {
        return new CompanyRepository() {
            @Override
            protected QueryAgent getQueryAgent() throws Exception {
                return queryAgent;
            }
        };
    }
}