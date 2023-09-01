package pro.sky.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import pro.sky.entity.AppUser;

public interface TaskService {
    String setTaskMode(AppUser appUser);
    String setTask(AppUser appUser, Long chatId, String task);
}
