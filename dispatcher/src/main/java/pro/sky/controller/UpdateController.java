package pro.sky.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.service.UpdateProducer;
import pro.sky.utils.MessageUtils;

import static pro.sky.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
        this.messageUtils = messageUtils;
        this.updateProducer=updateProducer;
    }
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot=telegramBot;
    }

    public void processUpdate(Update update){
        if(update==null){
            log.error("Received update is null");
            return;
        }

        if(update.hasMessage()){
            distributeMessageByType(update);
        } else{
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        } else if (message.hasDocument()){
            processDocumentMessage(update);
        } else if (message.hasPhoto()){
            processPhotoMessage(update);
        } else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,"Unsupported type of Message!");
        setView(sendMessage);
    }

    private void setFileReceivedViewUpdate(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,"File received. Processed...");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processDocumentMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileReceivedViewUpdate(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileReceivedViewUpdate(update);
    }
}
