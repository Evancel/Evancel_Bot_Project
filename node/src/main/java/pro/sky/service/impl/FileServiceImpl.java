package pro.sky.service.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import pro.sky.CryptoTool;
import pro.sky.dao.AppDocumentDAO;
import pro.sky.dao.AppPhotoDAO;
import pro.sky.dao.BinaryContentDAO;
import pro.sky.entity.AppDocument;
import pro.sky.entity.AppPhoto;
import pro.sky.entity.BinaryContent;
import pro.sky.enums.LinkType;
import pro.sky.exception.UploadFileException;
import pro.sky.service.FileService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;

    @Value("${service.file_info.url}")
    private String fileInfoUrl;

    @Value("${service.file_storage.url}")
    private String fileStorageUrl;
    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getResponse(fileId);
        if(response.getStatusCode()== HttpStatus.OK){
            BinaryContent persistenseBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc,persistenseBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else{
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO пока что обрабатываем только одно фото  из сообщения
        int photoSizeCount = telegramMessage.getPhoto().size();
        int photoIndex = photoSizeCount>1?photoSizeCount-1:0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getResponse(fileId);
        if(response.getStatusCode()== HttpStatus.OK){
            BinaryContent persistenseBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto,persistenseBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else{
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
       String hash = cryptoTool.hashOf(docId);
       return "http://" + linkAddress + "/" + linkType + "?id="+hash;
    }

    private ResponseEntity<String> getResponse(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        System.out.println("request = " + request);

        return restTemplate.exchange(
                fileInfoUrl,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response){
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        System.out.println(jsonObject);
        String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
        return filePath;
    }

    private byte[] downloadFile(String filePath) {
        String fullUrl = fileStorageUrl.replace("{token}",token)
                .replace("{filePath}",filePath);
        URL urlObj=null;
        try {
            urlObj=new URL(fullUrl);
        }catch(MalformedURLException e){
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try(InputStream is = urlObj.openStream();
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            return is.readAllBytes();
        }catch(IOException e){
            throw new UploadFileException(urlObj.toExternalForm(),e);
        }
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistenseBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistenseBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistenseBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistenseBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

}
