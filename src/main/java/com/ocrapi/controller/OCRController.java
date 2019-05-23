package com.ocrapi.controller;

import com.ocrapi.service.OCRService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/ocr/process", produces = MediaType.APPLICATION_JSON_VALUE)
public class OCRController {

    @Autowired
    OCRService ocrService;

    @RequestMapping(value = "/image", method = RequestMethod.GET)
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
    public ResponseEntity processFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        try {
            result = ocrService.processFile(file);
            return ResponseEntity.accepted().body(result);
        } catch (Exception ex) {
            result.put("result","error");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Data
    public static class OcrRequest {

        String image;
    }
}
