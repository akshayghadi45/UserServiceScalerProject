package org.java.userservicescalerproject.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.RandomStringUtils;
import org.java.userservicescalerproject.models.Token;
import org.java.userservicescalerproject.models.User;
import org.java.userservicescalerproject.repositories.TokenRepository;
import org.java.userservicescalerproject.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{


    //private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    /*Instead of creating the secret key here if we want to use the same secret key at multiple places we can create it in
    config file and even in config file we can import it form env variables and later */

    private static final long EXPIRATION_TIME_IN_MS = 10*60*60*1000;
    UserRepository userRepository;
    TokenRepository tokenRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    SecretKey SECRET_KEY;

    @Override
    public User validatetoken(String token) {

        if (token == null) {
            return null;
        }

        Claims claims;
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (io.jsonwebtoken.ExpiredJwtException e){
            System.out.println("Token is expired: " + e.getMessage());
            return null;
        }
        catch (io.jsonwebtoken.JwtException e){
            System.out.println("Token is invalid: " + e.getMessage());
            return null;
        }
        String email = claims.getSubject();
        if(email == null || email.isEmpty()){
            System.out.println("Email is null or empty");
            return null;
        }
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            System.out.println("User not found for the token: " + token);
            return null;
        }

        User user = optionalUser.get();
        if (Boolean.TRUE.equals(user.getDeleted())){
            System.out.println("User has been deleted: ");
        }
        return user;

    }

    UserServiceImpl(UserRepository userRepository , TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder,SecretKey secretKey) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.SECRET_KEY=secretKey;
    }
    @Override
    public User signup(String name, String email, String password) {
/// /Commenting to validate kafka as we want duplicate email to go if it fails for first time
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//        if (optionalUser.isPresent()) {
//            //throw user already present exception
//            return null;
//        }

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

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_MS);

        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();

        //Alternate way to generate expiry date
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 30);
//        Date date = calendar.getTime();

        Token  token = new Token();
        token.setUser(user);
        //token.setTokenValue(RandomStringUtils.randomAlphanumeric(128)); // Alternative way without using jwt
        token.setTokenValue(jwt);
        token.setExpiryAt(expiryDate);

        // return tokenRepository.save(token); // save token when its not self validating
        return token;
    }

    @Override
    public Boolean logout(String token) {
       Integer byTokenValueAndUpdatedDeleted = tokenRepository.updateTokenValueAndDeleted(token, true);
        if(byTokenValueAndUpdatedDeleted==1) {
           return true;
        }
        return false;
    }


    public User validateNonJwtToken(String token) {

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
