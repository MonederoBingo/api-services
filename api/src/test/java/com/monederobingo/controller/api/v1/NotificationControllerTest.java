package com.monederobingo.controller.api.v1;

import com.lealpoints.service.NotificationService;
import com.lealpoints.service.response.ServiceMessage;
import com.lealpoints.service.response.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NotificationControllerTest {
    @Test
    public void testSendMobileAppAdMessage() {
        NotificationService notificationService = createStrictMock(NotificationService.class);
        expect(notificationService.sendMobileAppAdMessage(anyInt(), anyString())).andReturn(new ServiceResult(true, ServiceMessage.EMPTY));
        replay(notificationService);
        NotificationController notificationController = new NotificationController(notificationService);
        final ResponseEntity<ServiceResult> responseEntity = notificationController.sendMobileAppAdMessage(0, "6623471507");
        assertNotNull(responseEntity);
        ServiceResult serviceResult = responseEntity.getBody();
        assertNotNull(serviceResult);
        assertTrue(serviceResult.isSuccess());
        verify(notificationService);
    }
}
