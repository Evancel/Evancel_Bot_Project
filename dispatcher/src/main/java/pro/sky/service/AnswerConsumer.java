package pro.sky.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {
    void consumer(SendMessage sendMessage);
}
