package pro.sky.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import pro.sky.entity.AppDocument;
import pro.sky.entity.AppPhoto;
import pro.sky.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
