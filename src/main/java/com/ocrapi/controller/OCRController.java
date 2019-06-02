package com.ocrapi.controller;

import com.ocrapi.model.OcrModel;
import com.ocrapi.service.OCRService;
import com.ocrapi.service.OcrModelService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://ocr-project-48077.firebaseapp.com"})
@RequestMapping(value = "/ocr", produces = MediaType.APPLICATION_JSON_VALUE)
public class OCRController {

    @Autowired
    OCRService ocrService;

    @Autowired
    OcrModelService ocrModelService;

    @RequestMapping(value = "/process/image", method = RequestMethod.GET)
    public ResponseEntity processOCR(@RequestBody OcrRequest payload) {
        Map<String, String> result = new HashMap<>();
        ocrService.setImage(payload.getImage());

        String resultOcr = ocrService.processOCR();
        if (resultOcr.isEmpty()) {
            result.put("result", "failed");
            return ResponseEntity.badRequest().body(result);
        } else {
            result.put("result", resultOcr);
            return ResponseEntity.accepted().body(result);
        }
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity processFile(@RequestParam("file") MultipartFile[] files,
                                      @RequestParam("parserName") String parserName,
                                      @RequestParam("parserType") String parserType,
                                      @RequestParam("createdBy") String createdBy) {
        Map<String, String> result = new HashMap<>();
        try {
            result = ocrService.processFile(files, parserName, parserType, createdBy);
            return ResponseEntity.accepted().body(result);
        } catch (Exception ex) {
            result.put("result", "error");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @RequestMapping(value = "/models", method = RequestMethod.GET)
    public List<OcrModel> getAllModels() {
        return ocrModelService.getAllModels();
    }

    @Data
    public static class OcrRequest {

        String parserName;
        String image;
        String createdBy;
    }
}
