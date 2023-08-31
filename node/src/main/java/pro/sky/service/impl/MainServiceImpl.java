package pro.sky.service.impl;


import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import pro.sky.dao.AppUserDAO;
import pro.sky.dao.RawDataDAO;
import pro.sky.entity.AppDocument;
import pro.sky.entity.AppPhoto;
import pro.sky.entity.AppUser;
import pro.sky.entity.RawData;
import pro.sky.enums.UserState;
import pro.sky.exception.UploadFileException;
import pro.sky.service.AppUserService;
import pro.sky.service.FileService;
import pro.sky.service.MainService;
import pro.sky.service.ProducerService;
import pro.sky.service.enums.LinkType;
import pro.sky.service.enums.ServiceCommands;

import java.util.Optional;

import static pro.sky.enums.UserState.BASIC_STATE;
import static pro.sky.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static pro.sky.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessages(Update update) {
        saveRawData(update);

        AppUser appuser = findOrSaveAppUser(update);
        UserState userState = appuser.getState();
        String text = update.getMessage().getText();
        String output="";

        ServiceCommands serviceCommands = ServiceCommands.fromValue(text);

        if(CANCEL.equals(serviceCommands)){
            output = cancelProcess(appuser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appuser, serviceCommands);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appuser,text);
        } else{
            log.error("Unknown user state: " + userState);
            output = "Unknown error. Input /cancel and try again!";
        }


        sendAnswer(output,update.getMessage().getChatId());
    }

    @Override
    public void processDocMessages(Update update) {
        saveRawData(update);

        AppUser appuser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if(isNotAllowedToSendContent(chatId, appuser)){
            return;
        }

        try{
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            String answer = "The document has been successfully loaded. Download link: " + link;
            sendAnswer(answer,chatId);
        } catch(UploadFileException ex){
            log.error(ex);
            String error= "Sorry, the file upload failed. Please try again later.";
            sendAnswer(error,chatId);
        }
    }

    @Override
    public void processPhotoMessages(Update update) {
        saveRawData(update);

        AppUser appuser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if(isNotAllowedToSendContent(chatId, appuser)){
            return;
        }

        try{
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            String answer = "Photo uploaded successfully. Download link: " + link;
            sendAnswer(answer,chatId);
        } catch(UploadFileException ex){
            log.error(ex);
            String error= "Sorry, the photo upload failed. Please try again later.";
            sendAnswer(error,chatId);
        }
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();

        Optional<AppUser> persistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if(persistentAppUser.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastname(telegramUser.getLastName())
                    .userName(telegramUser.getUserName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    private String processServiceCommand(AppUser appuser, ServiceCommands cmd) {
        if(REGISTRATION.equals(cmd)){
            return appUserService.registerUser(appuser);
        } else if (HELP.equals(cmd)){
            return help();
        } else if (START.equals(cmd)){
            return "Greetings! To see a list of available commands, type /help";
        } else {
            return "Unknown command. To see a list of available commands, type /help";
        }
    }

    private String help() {
        return "List of available commands:\n"
                +"/cancel canceling the current command;\n"
                +"/registration user registration. \n";
    }


    private String cancelProcess(AppUser appuser) {
        appuser.setState(BASIC_STATE);
        appUserDAO.save(appuser);
        return "Command canceled!";
    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appuser) {
        UserState userState = appuser.getState();
        if(!appuser.getIsActive()){
            String error = "Register or activate your account to download content.";
            sendAnswer(error,chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)){
            String error = "Cancel the current command with /cancel to send files.";
            sendAnswer(error,chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }
}
