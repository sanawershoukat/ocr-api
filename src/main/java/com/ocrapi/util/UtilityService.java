package com.ocrapi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UtilityService {

    @Value("${file.upload-dir}")
    String uploadDir;

    public List<File> convertMultiPartToFile(MultipartFile[] files) throws IOException {
        List<File> fileList = new ArrayList<>();

        Arrays.stream(files).forEach(multipartFile -> {
            File convFile = new File(System.getProperty("user.dir") + uploadDir + "/" + multipartFile.getOriginalFilename());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(convFile);
                fos.write(multipartFile.getBytes());
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            fileList.add(convFile);
        });

        return fileList;
    }
}
