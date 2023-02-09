package com.securedge.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securedge.server.model.User;
import com.securedge.server.response.GenericResponse;
import com.securedge.server.service.UserService;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class UserHandler extends BaseHttpOperationHandler {

    Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Value("${app.users}")
    private String USERS_URI;

    private final UserService userService;

    private final ObjectMapper mapper;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.mapper = new ObjectMapper();
    }

    protected GenericResponse handleGet(FullHttpRequest request) {
        logger.info("Handle user GET request");
        String uri = request.uri().replace(USERS_URI, "");
        if (uri.isEmpty() || uri.equals("/")) {
            return computeResponse(userService.findAll());
        } else if (uri.matches("/[0-9]+")) {
            Integer id = parseInt(uri.substring(1));
            if (Objects.isNull(id)) {
                return GenericResponse.error("Unable to parse id");
            }
            return computeResponse(userService.findById(id));
        } else if (uri.matches("/\\?sortByName=ASC") || uri.matches("/\\?sortByName=DESC")) {
            String sortOption = getSortOption(uri);
            return computeResponse(userService.findAll(sortOption));
        }
        return GenericResponse.error("Unable to process GET request");
    }

    protected GenericResponse handlePost(FullHttpRequest request) {
        logger.info("Handle user POST request");
        try {
            User user = mapper.readValue(getRequestBody(request), User.class);
            return computeResponse(userService.create(user));
        } catch (Exception ex) {
            return GenericResponse.error("Unable to create user");
        }
    }

    protected GenericResponse handlePut(FullHttpRequest request) {
        logger.info("Handle user PUT request");
        try {
            User user = mapper.readValue(getRequestBody(request), User.class);
            return computeResponse(userService.update(user));
        } catch (Exception ex) {
            return GenericResponse.error("Unable to update user");
        }
    }

    protected GenericResponse handleDelete(FullHttpRequest request) {
        logger.info("Handle user DELETE request");
        String uri = request.uri().replace(USERS_URI, "");
        if (uri.matches("/[0-9]+")) {
            Integer id = parseInt(uri.substring(1));
            if (Objects.isNull(id)) {
                return GenericResponse.error("Unable to parse id");
            }
            userService.delete(id);
        }
        return GenericResponse.ok();
    }

    private String getSortOption(String uri) {
        return uri.substring(uri.lastIndexOf("=") + 1);
    }
}
