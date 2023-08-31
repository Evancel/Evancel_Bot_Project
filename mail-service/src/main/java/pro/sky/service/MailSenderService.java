package pro.sky.service;

import pro.sky.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParms);
}
