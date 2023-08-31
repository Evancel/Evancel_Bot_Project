package pro.sky.service;

import org.springframework.core.io.FileSystemResource;
import pro.sky.entity.AppDocument;
import pro.sky.entity.AppPhoto;
import pro.sky.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);

}
