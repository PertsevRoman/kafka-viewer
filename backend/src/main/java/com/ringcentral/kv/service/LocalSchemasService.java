package com.ringcentral.kv.service;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class LocalSchemasService {

    private static final Logger log = LoggerFactory.getLogger(LocalSchemasService.class);
    
    @Value("${schemes.loaded.cache}")
    private String schemesLoadedCache;
    
    public String getSavePath() {
        return schemesLoadedCache.endsWith(File.separator) ? schemesLoadedCache : schemesLoadedCache + File.separator;
    }

    private String getFileSHA256(MultipartFile multipartFile) throws NoSuchAlgorithmException, IOException {
        byte[] multipartBytes = multipartFile.getBytes();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileHash = digest.digest(multipartBytes);

        return new String(Hex.encodeHex(fileHash));
    }
    
    public boolean saveFile(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        String sha256hex = getFileSHA256(multipartFile);

        byte[] multipartBytes = multipartFile.getBytes();

        String content = new String(multipartBytes);

        Schema.Parser parser = new Schema.Parser();

        try {
            parser.parse(content);
        } catch (SchemaParseException e) {
            log.error(e.getMessage());
            return false;
        }

        File saveDir = new File(getSavePath());
        
        if (!saveDir.exists()) {
            boolean created = saveDir.mkdirs();

            if (!created) {
                return false;
            }
        }

        String saveFileName = getSavePath() + sha256hex + "_" + multipartFile.getName();

        File saveFile = new File(saveFileName);
        
        if (saveFile.exists()) {
            return true;
        }
        
        multipartFile.transferTo(saveFile);

        return true;
    }
}
