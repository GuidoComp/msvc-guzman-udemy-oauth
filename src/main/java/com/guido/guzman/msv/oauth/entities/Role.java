package com.guido.guzman.msv.oauth.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    private List<User> users;
}
