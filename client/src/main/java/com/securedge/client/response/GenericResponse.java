package com.securedge.client.response;

import com.securedge.client.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenericResponse {

    private List<User> body;
    private String message;
    private boolean hasError;
}
