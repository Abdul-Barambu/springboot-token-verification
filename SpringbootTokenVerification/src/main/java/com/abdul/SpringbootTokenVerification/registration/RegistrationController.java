package com.abdul.SpringbootTokenVerification.registration;

import com.abdul.SpringbootTokenVerification.appuser.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/register")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;


    @GetMapping(path = "/home")
    public String home(){
        return "This is home page";
    }

    @PostMapping
    public ResponseEntity<Object> register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }


    @GetMapping(path = "confirm")
    public ResponseEntity<Object> confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }

    @GetMapping(path = "/getAllUsers")
    public List<AppUser> getUsers(){
        return registrationService.getUsers();
    }
}
