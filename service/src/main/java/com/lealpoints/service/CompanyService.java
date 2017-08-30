package com.lealpoints.service;

import com.lealpoints.model.CompanyUser;
import com.lealpoints.service.model.CompanyRegistration;
import com.lealpoints.service.response.ServiceResult;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.util.List;

public interface CompanyService extends BaseService {

    ServiceResult<String> register(CompanyRegistration companyRegistration);

    xyz.greatapp.libs.service.ServiceResult getPointsInCompanyByPhone(String phone);

    xyz.greatapp.libs.service.ServiceResult getByCompanyId(long companyId);

    ServiceResult<Boolean> updateLogo(List<FileItem> fileItems, long companyId);

    File getLogo(long companyId);

    void setUserActivation(CompanyUser companyUser);
}