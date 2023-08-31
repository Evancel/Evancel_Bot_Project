package pro.sky.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import pro.sky.CryptoTool;
import pro.sky.dao.AppDocumentDAO;
import pro.sky.dao.AppPhotoDAO;
import pro.sky.entity.AppDocument;
import pro.sky.entity.AppPhoto;
import pro.sky.entity.BinaryContent;
import pro.sky.exception.DocumentNotFoundException;
import pro.sky.service.FileService;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) {
        Long docId = cryptoTool.idOf(hash);
        if(docId==null){
            return null;
        }
        return appDocumentDAO.findById(docId)
                .orElseThrow(()-> new DocumentNotFoundException("Document not found. Try again."));
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        Long photoId = cryptoTool.idOf(hash);
        if(photoId==null){
            return null;
        }
        return appPhotoDAO.findById(photoId).
                orElseThrow(()->new DocumentNotFoundException("Photo not found. Try again."));
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO добавить генерацию имени временного файла
            /*/bin (от англ. binary files — «двоичные файлы») — каталог в UNIX-подобных системах,
            содержащий исполняемые файлы. В соответствии с FHS монтируется на корневую файловую систему
             и должен быть доступен даже если никакие другие файловые системы не смонтированы.
             Простой пользователь может просматривать расположенные в нём файлы, но не изменять их.
             Доступ на запись имеют только суперпользователи.
             */
            File temp = File.createTempFile("tempFile", "bin");
            /*удалить временный файл перед выходом из программы*/
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch(IOException e){
            log.error(e);
            return null;
        }
    }
}
