package org.java.userservicescalerproject.services;

import org.java.userservicescalerproject.models.Token;
import org.java.userservicescalerproject.models.User;
import org.springframework.stereotype.Service;


public interface UserService {
    User signup(String name, String email, String password);
    Token login(String email, String password);
    Boolean logout(String token);
    User validatetoken(String token);
}
