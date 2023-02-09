package com.securedge.server.handler;

import com.securedge.server.response.GenericResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public abstract class BaseHttpOperationHandler {

    Logger logger = LoggerFactory.getLogger(BaseHttpOperationHandler.class);

    public GenericResponse handle(FullHttpRequest request) {
        HttpMethod method = request.method();
        if (method == HttpMethod.GET) {
            return handleGet(request);
        } else if (method == HttpMethod.POST) {
            return handlePost(request);
        } else if (method == HttpMethod.PUT) {
            return handlePut(request);
        } else if (method == HttpMethod.DELETE) {
            return handleDelete(request);
        }
        logger.warn("Unrecognized http method detected: " + method.name());
        return GenericResponse.error("Unrecognized http method detected: " + method.name());
    }

    protected GenericResponse computeResponse(Object response) {
        return GenericResponse.ok(response);
    }

    protected String getRequestBody(FullHttpRequest request) {
        byte[] bytes = new byte[request.content().readableBytes()];
        request.content().readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            logger.error("Unable to parse value: " + value);
            return null;
        }
    }

    protected abstract GenericResponse handleGet(FullHttpRequest request);

    protected abstract GenericResponse handlePost(FullHttpRequest request);

    protected abstract GenericResponse handlePut(FullHttpRequest request);

    protected abstract GenericResponse handleDelete(FullHttpRequest request);
}
