package org.java.userservicescalerproject.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.java.userservicescalerproject.models.Token;
import org.java.userservicescalerproject.models.User;
import org.java.userservicescalerproject.repositories.TokenRepository;
import org.java.userservicescalerproject.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    TokenRepository tokenRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    UserServiceImpl(UserRepository userRepository , TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public User signup(String name, String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            //throw user already present exception
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
          //throw new user not found exception
          return null;
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())) {
            //throw exception incorrect password
            return null;
        }

        Token  token = new Token();
        token.setUser(user);
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        Date date = calendar.getTime();
        token.setExpiryAt(date);

        return tokenRepository.save(token);
    }

    @Override
    public Boolean logout(String token) {
       Integer byTokenValueAndUpdatedDeleted = tokenRepository.updateTokenValueAndDeleted(token, true);
        if(byTokenValueAndUpdatedDeleted==1) {
           return true;
        }
        return false;
    }

    @Override
    public User validatetoken(String token) {

        /*
            1:Exists in DB
            2:Should not be deleted
            3:not expired
            4: belongs to same user
         */

        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndDeletedAndExpiryAtGreaterThan(token,false,new Date());
        if(!optionalToken.isPresent()) {
            return null;
        }
        return optionalToken.get().getUser();
    }
}
