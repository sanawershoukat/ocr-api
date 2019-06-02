package com.ocrapi.service.impl;

import com.asprise.ocr.Ocr;
import com.ocrapi.controller.OCRController;
import com.ocrapi.model.OcrModel;
import com.ocrapi.service.OCRService;
import com.ocrapi.service.OcrModelService;
import com.ocrapi.util.UtilityService;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OCRServiceImpl implements OCRService {

    Logger log = LoggerFactory.getLogger(OCRService.class);

    String image;

    Tesseract tesseract = new Tesseract();

    Ocr ocr = new Ocr();

    @Autowired
    UtilityService utilityService;

    @Autowired
    OcrModelService ocrModelService;

    @Override
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String processOCR() {
        String result = "";
        try {
            log.info("User Directory: " + System.getProperty("user.dir"));
            File file = new File(image);
            if (file.exists()) {
                tesseract.setLanguage("eng");
                tesseract.setDatapath("/usr/local/Cellar/tesseract/4.0.0_1/share/tessdata");
                result = tesseract.doOCR(file);
                System.out.println("===================================================");
                System.out.println("=================== OCR Output ====================");
                System.out.println("===================================================");
                System.out.println(result);
            } else {
                log.info("File doesn't exist");
            }
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
        }
//        ocr.startEngine("eng", Ocr.SPEED_FASTEST);
//        String s = ocr.recognize(new File[] {new File("test-image.png")},
//                Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);
//
//        System.out.println("---> OCR Processed Text <--- \n" + s);
//        ocr.stopEngine();
        return result;
    }

    public Map processFile(MultipartFile[] multipartFiles, String parserName, String parserType, String createdBy) {

        final boolean consolidated;
        Map<String, Object> result = new HashMap<>();
        List<String> listProcessedText = new ArrayList<>();
        List<Long> listMatchRate = new ArrayList<>();

        try {
            List<File> fileList = utilityService.convertMultiPartToFile(multipartFiles);
            consolidated = (fileList != null && fileList.size() > 1);

            fileList.forEach(file -> {
                log.info(file.getName());
                setImage(file.getAbsolutePath());
                String text = processOCR();
                long matchRate = checkAndGetModelMatchingPercentage(text);
                if (matchRate < 90)
                    insertNewModel(text, parserName, parserType, createdBy, consolidated);

                listProcessedText.add(text);
                listMatchRate.add(matchRate);

            });

            result.put("processed", listProcessedText);
            result.put("percentage", listMatchRate);
            result.put("result", "success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private long checkAndGetModelMatchingPercentage(String text) {
        AtomicLong atomicLong = new AtomicLong(0);

        List<OcrModel> models = ocrModelService.getAllModels();
        models.stream().forEach(model -> {
            JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
//            LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

//            int i = levenshteinDistance.apply(model.getText(), text);
//            log.info("LevenshteinDistance Result: " + i);
            System.out.println("================================");
            System.out.println(model.getText());
            System.out.println("================================");
            double rate = jaroWinklerDistance.apply(text, model.getText()).longValue();
            log.info("Matching Rate: " + rate);
            long percentage = (long) rate * 100;
            if (percentage > 90) {
                log.info("Match rate: " + percentage);
                atomicLong.set(percentage);
            }
        });

        return atomicLong.get();
    }

    void insertNewModel(String text, String parserName, String parserType, String createdBy, boolean consolidated) {
        OcrModel model = new OcrModel();
        model.setParserName(parserName);
        model.setParserType(parserType);
        model.setText(text);
        model.setCreatedBy(createdBy);
        model.setConsolidated(consolidated);
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());
        ocrModelService.insertModel(model);
    }
}
