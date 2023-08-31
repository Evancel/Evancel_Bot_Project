package pro.sky.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.service.ConsumerService;
import pro.sky.service.MainService;

import static pro.sky.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessagedUpdates(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessages(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessagedUpdates(Update update) {
        log.debug("NODE: Doc message is received");
        mainService.processDocMessages(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessagedUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessages(update);
    }
}
