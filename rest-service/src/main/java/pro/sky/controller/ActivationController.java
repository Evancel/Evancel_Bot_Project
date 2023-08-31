package pro.sky.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.CryptoTool;
import pro.sky.service.UserActivationService;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService, CryptoTool cryptoTool) {
        this.userActivationService = userActivationService;
    }
    @RequestMapping(method = RequestMethod.GET, value="/activation")
    public ResponseEntity<?> activation(@RequestParam ("id") String cryptoUserId){
        if(userActivationService.activation(cryptoUserId)){
            return ResponseEntity.ok().body("Registration successfully completed!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
