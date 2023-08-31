package pro.sky.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pro.sky.CryptoTool;
import pro.sky.dao.AppUserDAO;
import pro.sky.dto.MailParams;
import pro.sky.entity.AppUser;
import pro.sky.enums.UserState;
import pro.sky.service.AppUserService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if(appUser.getIsActive()){
            return "You are already registered";
        }else if(appUser.getEmail()!=null){
            return "Your email with the activation code has been already sent to this email. "
                    +"Please follow the link in the email to confirm your registration.";
        }

        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Please enter your email: ";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try{
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        }catch(AddressException e){
           return "Enter the correct email please. Input /cancel for return.";
        }

        Optional<AppUser> user = appUserDAO.findByEmail(email);
        if(user.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if(response.getStatusCode()!= HttpStatus.OK){
                String msg = String.format("Sending the letter to the email %s was failed.",email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return msg;
            }
            return "The message was sent to your email. Please check and confirm your email.";
        } else {
            return "This email is already used. Input the correct email. Type /cancel to return to the main menu.";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
       HttpEntity<MailParams> request = new HttpEntity<>(mailParams, headers);
       return restTemplate.exchange(mailServiceUri,
                                    HttpMethod.POST,
                                    request,
                                    String.class);
    }
}
