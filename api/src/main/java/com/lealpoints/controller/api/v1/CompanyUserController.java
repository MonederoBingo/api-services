package com.lealpoints.controller.api.v1;

        import com.lealpoints.controller.base.BaseController;
        import com.lealpoints.service.model.CompanyUserRegistration;
        import com.lealpoints.service.response.ServiceMessage;
        import com.lealpoints.service.response.ServiceResult;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import javax.ws.rs.Produces;
        import javax.ws.rs.core.MediaType;

        import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/v1/company_users")
public class CompanyUserController extends BaseController {

    @RequestMapping(value="/register", method = POST, headers = ACCEPT_HEADER)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<ServiceResult> register(@RequestBody CompanyUserRegistration companyUserRegistration) {
        return new ResponseEntity<>(new ServiceResult(false,new ServiceMessage("Operation not supported yet")), HttpStatus.OK);
    }
}
