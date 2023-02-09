package com.securedge.server.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericResponse {

    private Object body;
    private String message;
    private boolean hasError;

    public static GenericResponse ok() {
        return GenericResponse.ok("Success");
    }

    public static GenericResponse ok(Object body) {
        GenericResponse response = new GenericResponse();
        response.body = body;
        response.message = "{}";
        response.hasError = false;
        return response;
    }

    public static GenericResponse error(String message) {
        GenericResponse response = new GenericResponse();
        response.message = message;
        response.hasError = true;
        return response;
    }
}
