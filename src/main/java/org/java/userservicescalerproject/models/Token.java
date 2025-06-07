package org.java.userservicescalerproject.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import javax.xml.crypto.Data;
import java.util.Date;

@Getter
@Setter
@Entity
public class Token extends  Base{
    private String tokenValue;
    private Date expiryAt;

    @ManyToOne
    private User user;
}

/*
TOken ----------User
1---------------1
M---------------1
 */