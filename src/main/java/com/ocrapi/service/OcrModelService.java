package com.ocrapi.service;

import com.ocrapi.model.OcrModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OcrModelService {

    void insertModel(OcrModel model);

    List<OcrModel> getAllModels();
}
