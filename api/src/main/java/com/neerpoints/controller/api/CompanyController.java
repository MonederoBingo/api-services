package com.neerpoints.controller.api;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import com.neerpoints.model.Company;
import com.neerpoints.service.CompanyService;
import com.neerpoints.service.model.ServiceResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/companies")
@MultipartConfig
public class CompanyController extends AbstractApiController {

    private CompanyService _companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        _companyService = companyService;
    }

    @RequestMapping(value = "/{companyId}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult<Company>> get(@PathVariable("companyId") long companyId) {
        try {
            ServiceResult<Company> serviceResult = _companyService.getByCompanyId(companyId);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ServiceResult<Company>>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/logo/{companyId}", method = POST)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> updateLogo(@PathVariable("companyId") long companyId, HttpServletRequest httpServletRequest) {
        try {
            ServletFileUpload servletFileUpload = getServletFileUpload();
            List<FileItem> items = servletFileUpload.parseRequest(httpServletRequest);
            final ServiceResult serviceResult = _companyService.updateLogo(items, companyId);
            return new ResponseEntity<>(serviceResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/logo/{companyId}", method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<byte[]> getLogo(@PathVariable("companyId") long companyId, HttpServletRequest request, HttpServletResponse response) {
        try {
            File file = _companyService.getLogo(companyId);
            InputStream input = new FileInputStream(file);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getMediaTypeFromExtension(FilenameUtils.getExtension(file.getName())));
            return new ResponseEntity<>(IOUtils.toByteArray(input), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    ServletFileUpload getServletFileUpload() {
        return new ServletFileUpload(new DiskFileItemFactory());
    }

    private org.springframework.http.MediaType getMediaTypeFromExtension(String extension) {
        if (extension.equalsIgnoreCase("png")) {
            return org.springframework.http.MediaType.IMAGE_PNG;
        } else if (extension.equalsIgnoreCase("gif")) {
            return org.springframework.http.MediaType.IMAGE_GIF;
        } else {
            return org.springframework.http.MediaType.IMAGE_PNG;
        }
    }

}
