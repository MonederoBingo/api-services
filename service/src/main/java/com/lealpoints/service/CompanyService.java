package com.lealpoints.service;

import java.io.File;
import java.util.List;
import com.lealpoints.model.Company;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.service.annotation.OnlyProduction;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.model.ServiceResult;
import org.apache.commons.fileupload.FileItem;

public interface CompanyService extends BaseService {

    public ServiceResult<Long> register(CompanyRegistration companyRegistration);

    public ServiceResult<List<PointsInCompany>> getPointsInCompanyByPhone(String phone);

    public ServiceResult<Company> getByCompanyId(long companyId);

    public ServiceResult<Boolean> updateLogo(List<FileItem> fileItems, long companyId);

    @OnlyProduction
    public ServiceResult sendMobileAppAdMessage(long companyId, String phone);

    String getSMSMessage(String companyName, double points);

    public File getLogo(long companyId);
}