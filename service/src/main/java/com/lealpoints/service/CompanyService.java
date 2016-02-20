package com.lealpoints.service;

import com.lealpoints.model.Company;
import com.lealpoints.model.CompanyUser;
import com.lealpoints.model.PointsInCompany;
import com.lealpoints.service.annotation.OnlyProduction;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.response.ServiceResult;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.util.List;

public interface CompanyService extends BaseService {

    ServiceResult<String> register(CompanyRegistration companyRegistration);

    ServiceResult<List<PointsInCompany>> getPointsInCompanyByPhone(String phone);

    ServiceResult<Company> getByCompanyId(long companyId);

    ServiceResult<Boolean> updateLogo(List<FileItem> fileItems, long companyId);

    File getLogo(long companyId);

    void setUserActivation(CompanyUser companyUser);
}