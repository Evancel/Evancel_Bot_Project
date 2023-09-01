package pro.sky.service;

import pro.sky.entity.AppUser;

public interface TaskService {
    String setTaskMode(AppUser appUser);
    String setTask(AppUser appUser, String task);

}
