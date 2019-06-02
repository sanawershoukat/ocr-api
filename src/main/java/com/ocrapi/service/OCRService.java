package com.ocrapi.service;

import com.ocrapi.controller.OCRController;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface OCRService {

    void setImage(String image);

    String processOCR();

    Map processFile(MultipartFile[] multipartFile, String parserName, String parserType, String createdBy);
}
