package com.ocrapi.repository;

import com.ocrapi.model.OcrModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrModelRepository extends MongoRepository<OcrModel, String> {

}
