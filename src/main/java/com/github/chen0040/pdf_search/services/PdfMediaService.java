package com.github.chen0040.pdf_search.services;

import com.github.chen0040.pdf_search.entities.PdfMediaEntity;

public interface PdfMediaService {
    long savePdf(byte[] bytes, String username, String documentId);
    PdfMediaEntity findByDocumentId(String documentId);

}
