package org.java.userservicescalerproject.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends  Base{
    private String name;
    private String password;
    private String email;

    @ManyToMany
    private List<Role> roles;
}


/*Cardinality
USer ------- ROle
1-----------M
M ----------1

User : Roke = M:M
 */