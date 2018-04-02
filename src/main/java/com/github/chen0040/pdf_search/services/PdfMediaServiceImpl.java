package com.github.chen0040.pdf_search.services;

import com.github.chen0040.pdf_search.entities.PdfMediaEntity;
import com.github.chen0040.pdf_search.repositories.PdfMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class PdfMediaServiceImpl implements PdfMediaService {

    @Autowired
    private PdfMediaRepository repository;

    @Transactional
    @Override
    public long savePdf(byte[] bytes, String username, String documentId) {

        PdfMediaEntity entity = repository.findFirstByDocumentId(documentId);
        Date now = new Date();
        if(entity == null){
            entity = new PdfMediaEntity();
            entity.setCreatedAt(now);
        }
        entity.setModel(bytes);
        entity.setUpdatedAt(now);
        entity.setDocumentId(documentId);

        entity = repository.save(entity);

        return entity.getId();
    }

    @Override
    public PdfMediaEntity findByDocumentId(String documentId) {
        return repository.findFirstByDocumentId(documentId);
    }
}
