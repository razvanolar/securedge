package com.securedge.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securedge.server.response.GenericResponse;
import com.securedge.server.service.AccountService;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AccountHandler extends BaseHttpOperationHandler {

    Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Value("${app.accounts}")
    private String ACCOUNTS_URI;

    private final AccountService accountService;

    private final ObjectMapper mapper;

    @Autowired
    public AccountHandler(AccountService accountService) {
        this.accountService = accountService;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected GenericResponse handleGet(FullHttpRequest request) {
        logger.info("Handle account GET request");
        String uri = request.uri().replace(ACCOUNTS_URI, "");
        if (uri.isEmpty() || uri.equals("/")) {
            return computeResponse(accountService.findAll());
        } else if (uri.matches("/[0-9]+")) {
            Integer id = parseInt(uri.substring(1));
            if (Objects.isNull(id)) {
                return GenericResponse.error("Unable to parse id");
            }
            return computeResponse(accountService.findById(id));
        }
        return GenericResponse.error("Unable to process GET request");
    }

    @Override
    protected GenericResponse handlePost(FullHttpRequest request) {
        return null;
    }

    @Override
    protected GenericResponse handlePut(FullHttpRequest request) {
        return null;
    }

    @Override
    protected GenericResponse handleDelete(FullHttpRequest request) {
        return null;
    }
}
