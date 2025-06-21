package org.java.userservicescalerproject.controllers;

import org.java.userservicescalerproject.dtos.*;
import org.java.userservicescalerproject.models.Token;
import org.java.userservicescalerproject.models.User;
import org.java.userservicescalerproject.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    UserService userService;
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto){
        Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
       LoginResponseDto loginResponseDto = new LoginResponseDto();
       loginResponseDto.setToken(token.getTokenValue());
       return loginResponseDto;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignUpDto signUpDto){

        User user =  userService.signup(signUpDto.getName(),signUpDto.getEmail(), signUpDto.getPassword());
        return UserDto.from(user);
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity<Void> logout(@PathVariable String token){

        Boolean logout = userService.logout(token);
        if(logout){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){

        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        User user = userService.validatetoken(token);
        if(user != null){
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }
}
