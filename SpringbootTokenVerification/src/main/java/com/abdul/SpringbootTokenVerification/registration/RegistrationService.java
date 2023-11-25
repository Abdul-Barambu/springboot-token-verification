package com.abdul.SpringbootTokenVerification.registration;

import com.abdul.SpringbootTokenVerification.appuser.AppUser;
import com.abdul.SpringbootTokenVerification.appuser.AppUserRole;
import com.abdul.SpringbootTokenVerification.appuser.AppUserService;
import com.abdul.SpringbootTokenVerification.token.TokenConfirmation;
import com.abdul.SpringbootTokenVerification.token.TokenConfirmationRepository;
import com.abdul.SpringbootTokenVerification.token.TokenConfirmationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final EmailValidation emailValidation;
    private final AppUserService appUserService;
    private final TokenConfirmationService tokenConfirmationService;
    private final TokenConfirmationRepository tokenConfirmationRepository;

    public ResponseEntity<Object> register(RegistrationRequest request) {
//        check if email is valid
        boolean isEmailValid = emailValidation.test(request.getEmail());
        if (!isEmailValid) {
            throw new IllegalStateException("Email is not valid");
        }

        return appUserService.signUpUser(
                new AppUser(
                        request.getName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.PAYEE
                )
        );

    }

    public ResponseEntity<Object> confirmToken(String token) {
        try {
            TokenConfirmation tokenConfirmation = tokenConfirmationService.getToken(token).orElseThrow(
                    () -> new IllegalStateException("Token not found")
            );

            if (tokenConfirmation.getConfirmedAt() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createResponseToken("error", "Token already confirmed", token, tokenConfirmation.getConfirmedAt()));
            }

            LocalDateTime expires = tokenConfirmation.getExpiresAt();
            if (expires.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createResponseExpire("error", "Token expired already", token, tokenConfirmation.getExpiresAt()));
            }

            tokenConfirmationService.setConfirmedAt(token);
            appUserService.enabledUser(tokenConfirmation.getAppUser().getEmail());

            return ResponseEntity.ok(createResponse("success", "Your token has been confirmed", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponse("error", e.getMessage(), null));
        }
    }

    public List<AppUser> getUsers(){
        return appUserService.getAllUsers();
    }

    private Map<String, Object> createResponse(String status, String message, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);

        Map<String, Object> data = new HashMap<>();
        List<String> messages = new ArrayList<>();
        messages.add(message);

        data.put("message", messages);
        data.put("token", token);

        response.put("data", data);

        return response;
    }

    private Map<String, Object> createResponseToken(String status, String message, String token, LocalDateTime TokenConfirmedAt) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);

        Map<String, Object> data = new HashMap<>();
        List<String> messages = new ArrayList<>();
        messages.add(message);

        data.put("message", messages);
        data.put("token", token);
        data.put("Token Confirmed Time", TokenConfirmedAt);

        response.put("data", data);

        return response;
    }

    private Map<String, Object> createResponseExpire(String status, String message, String token, LocalDateTime expiresAt) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);

        Map<String, Object> data = new HashMap<>();
        List<String> messages = new ArrayList<>();
        messages.add(message);

        data.put("message", messages);
        data.put("token", token);
        data.put("Token expired At", expiresAt);

        response.put("data", data);

        return response;
    }


}
