package pro.sky.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessagedUpdates(Update update);
    void consumeDocMessagedUpdates(Update update);
    void consumePhotoMessagedUpdates(Update update);
}
