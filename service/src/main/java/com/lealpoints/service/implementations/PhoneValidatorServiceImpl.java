package com.lealpoints.service.implementations;

import xyz.greatapp.libs.service.context.ThreadContextService;
import com.lealpoints.i18n.Message;
import com.lealpoints.service.PhoneValidatorService;
import com.lealpoints.service.model.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneValidatorServiceImpl extends BaseServiceImpl implements PhoneValidatorService {

    @Autowired
    public PhoneValidatorServiceImpl(ThreadContextService threadContextService) {
        super(threadContextService);
    }

    public ValidationResult validate(String phone) {
        if (phone == null || phone.length() != 10) {
            return new ValidationResult(false, getServiceMessage(Message.PHONE_MUST_HAVE_10_DIGITS));
        }
        return new ValidationResult(true);
    }
}
