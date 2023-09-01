package pro.sky.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessages(Update update);
    void processPhotoMessages(Update update);
    void processDocMessages(Update update);
    void remindAboutCurrentTasks();
}
