package com.github.chen0040.pdf_search.repositories;

import com.github.chen0040.pdf_search.entities.PdfMediaEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfMediaRepository extends CrudRepository<PdfMediaEntity, Long> {
    PdfMediaEntity findFirstByDocumentId(String documentId);

}
