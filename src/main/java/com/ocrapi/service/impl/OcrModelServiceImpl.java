package com.ocrapi.service.impl;

import com.ocrapi.model.OcrModel;
import com.ocrapi.repository.OcrModelRepository;
import com.ocrapi.service.OcrModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OcrModelServiceImpl implements OcrModelService {

    @Autowired
    OcrModelRepository repository;

    @Override
    public void insertModel(OcrModel model) {
        repository.insert(model);
    }

    @Override
    public List<OcrModel> getAllModels() {
        return repository.findAll();
    }


}
