package com.lealpoints.service.implementations;

import com.lealpoints.context.ThreadContext;
import com.lealpoints.context.ThreadContextService;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class ServiceBaseTest {

    protected static ThreadContextService createThreadContextService(ThreadContext threadContext) {
        ThreadContextService threadContextService = createMock(ThreadContextService.class);
        expect(threadContextService.getThreadContext()).andReturn(threadContext);
        replay(threadContextService);
        return threadContextService;
    }
}
