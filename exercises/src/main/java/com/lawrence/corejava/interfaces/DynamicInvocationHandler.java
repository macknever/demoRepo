package com.lawrence.corejava.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicInvocationHandler implements InvocationHandler {

    private static Logger LOG = LoggerFactory.getLogger(DynamicInvocationHandler.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        LOG.info("Invoked method: {}", method.getName());

        return 42;
    }
}
