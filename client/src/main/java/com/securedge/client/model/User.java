package com.securedge.client.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    private int id;

    private String name;

    private String email;

    private String password;

    private int accountId;

    private Timestamp createdDate;
}
