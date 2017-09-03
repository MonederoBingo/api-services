package com.lealpoints.service.implementations;

import com.lealpoints.environments.ProdEnvironment;
import com.lealpoints.service.ConfigurationService;
import org.easymock.EasyMock;
import org.junit.Test;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.context.ThreadContext;
import xyz.greatapp.libs.service.context.ThreadContextService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static org.easymock.EasyMock.*;

public class SMSServiceImplTest extends ServiceBaseTest {

    @Test
    public void testSendSMSMessage() throws IOException, MessagingException {

        final HttpURLConnection httpURLConnection = creteHttpURLConnection();
        final ConfigurationService configurationService = createConfigurationService();
        final ThreadContext threadContext = createThreadContext();
        final ThreadContextService threadContextService = createThreadContextService(threadContext);
        SMSServiceImpl smsService = new SMSServiceImpl(threadContextService, configurationService) {

            @Override
            URL getUrl(String phone, String message) throws MalformedURLException {
                return new URL("http://www.google.com");
            }

            @Override
            InputStreamReader getInputStreamReader(HttpURLConnection connection) throws IOException {
                return super.getInputStreamReader(connection);
            }

            @Override
            HttpURLConnection getHttpURLConnection(URL url) throws IOException {
                return httpURLConnection;
            }

            @Override
            public String getResponse(HttpURLConnection connection) throws IOException {
                return "OK";
            }
        };

        smsService.sendSMSMessage("6623471507", "message");
        verify(httpURLConnection);
    }

    private ThreadContext createThreadContext() {
        ThreadContext threadContext = createMock(ThreadContext.class);
        expect(threadContext.getEnvironment()).andReturn(Environment.PROD);
        replay(threadContext);
        return threadContext;
    }

    private ConfigurationService createConfigurationService() {
        ConfigurationService configurationService = createMock(ConfigurationService.class);
        expect(configurationService.getUncachedConfiguration(anyString())).andReturn("false");
        replay(configurationService);
        return configurationService;
    }

    private HttpURLConnection creteHttpURLConnection() throws ProtocolException {
        final HttpURLConnection httpURLConnection = EasyMock.createStrictMock(HttpURLConnection.class);
        httpURLConnection.setRequestMethod(anyString());
        expectLastCall();
        httpURLConnection.disconnect();
        expectLastCall();
        replay(httpURLConnection);
        return httpURLConnection;
    }
}