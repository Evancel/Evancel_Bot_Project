package pro.sky.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import pro.sky.dao.AppTaskDAO;
import pro.sky.dao.AppUserDAO;
import pro.sky.entity.AppTask;
import pro.sky.entity.AppUser;
import pro.sky.enums.UserState;
import pro.sky.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
@Service
public class TaskServiceImpl implements TaskService {
    private final AppUserDAO appUserDAO;
    private final AppTaskDAO appTaskDAO;

    public TaskServiceImpl(AppUserDAO appUserDAO, AppTaskDAO appTaskDAO) {
        this.appUserDAO = appUserDAO;
        this.appTaskDAO = appTaskDAO;
    }

    @Override
    public String setTaskMode(AppUser appUser) {
        appUser.setState(UserState.WAIT_FOR_TASK_STATE);
        appUserDAO.save(appUser);
        return "Type your task. Example: 01.01.2022 20:00 To meet friends";
    }

    @Override
    public String setTask(AppUser appUser, String text) {
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)(([a-z A-Z0-9_+]+)||([\\W+]+))");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String task = matcher.group(3);

            LocalDateTime datetime = LocalDateTime.parse(date,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            AppTask appTask = AppTask.builder()
                    .dateTime(datetime)
                    .task(task)
                    .build();

            appTaskDAO.save(appTask);
            return "The task is added.";
        } else {
            return "The wrong format. Try again.";
        }
    }
}
