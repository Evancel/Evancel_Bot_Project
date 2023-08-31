package pro.sky.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.sky.service.ProducerService;

import static pro.sky.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;
    public ProducerServiceImpl(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }
    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
}
