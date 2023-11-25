package com.abdul.SpringbootTokenVerification.appuser;

import com.abdul.SpringbootTokenVerification.token.TokenConfirmation;
import com.abdul.SpringbootTokenVerification.token.TokenConfirmationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenConfirmationService tokenConfirmationService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        find the email
        return appUserRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Email not found"));

    }

    public ResponseEntity<Object> signUpUser(AppUser appUser){
//        Check if email exist
        boolean emailExist = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if(emailExist){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseEmail( "error", "Email already exist, please change email", appUser.getEmail()));
        }


        String passwordEncoded = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(passwordEncoded);

        appUserRepository.save(appUser);


//        Token
        String token = UUID.randomUUID().toString();

        TokenConfirmation tokenConfirmation = new TokenConfirmation(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                appUser
        );

        tokenConfirmationService.saveToken(tokenConfirmation);

        return ResponseEntity.ok(createResponseToken("success", "Registration Successfully", token,
                "please confirm your token using the url below", "localhost:8080/api/v1/register/confirm?token="+token));

    }

//    Response for Email
    private Map<String, Object> createResponseEmail(String status, String message, String email){
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);

        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("email", email);

        response.put("data", data);

        return response;
    }

    private Map<String, Object> createResponseToken(String status, String msg, String token, String message, String url){
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);


        Map<String, Object> data = new HashMap<>();
        data.put("msg", msg);
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        data.put("token", tokens);
        data.put("message", message);
        data.put("url", url);

        response.put("data", data);

        return response;
    }

    public int enabledUser(String email){
        return appUserRepository.enableUser(email);
    }

    public List<AppUser> getAllUsers(){
       return appUserRepository.findAll();
    }
}
