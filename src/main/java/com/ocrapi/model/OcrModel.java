package com.ocrapi.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
public class OcrModel {

    @Id
    String id;
    String text;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
