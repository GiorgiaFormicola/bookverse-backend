package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.users.*;
import giorgiaformicola.capstone.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }

    //ENDPOINT REGISTRAZIONE
    //TODO: update registration email sender
    //TODO: verify email
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Validated RegistrationDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.save(body);
    }

    //ENDPOINT LOGIN
    @PostMapping("login")
    public AccessTokenDTO login(@RequestBody @Validated LoginDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return new AccessTokenDTO(this.usersService.checkUserCredentialsAndGenerateToken(body));
    }

    //ENDPOINT RICHIESTA RIATTIVAZIONE ACCOUNT
    @PostMapping("reactivation-request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivationRequest(@RequestBody @Validated SupportRequestDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        this.usersService.sendReactivationRequest(body);
    }

    //ENDPOINT RICHIESTA PASSWORD DIMENTICATA
    @PostMapping("forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@RequestBody @Validated SupportRequestDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        this.usersService.sendResetPasswordEmail(body);
    }

    //ENDPOINT RICHIESTA RESET PASSWORD
    @PostMapping("reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody @Validated ResetPasswordDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        this.usersService.resetPassword(body);
    }
}
