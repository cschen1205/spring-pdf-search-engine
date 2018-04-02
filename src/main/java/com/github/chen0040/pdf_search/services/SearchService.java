package com.github.chen0040.pdf_search.services;

import com.github.chen0040.pdf_search.models.PdfSearchEntry;

import java.util.List;
import java.util.Map;

public interface SearchService {
    void indexAsync(String documentId, String username);
    long countByQuery(String keyword, Map<String, String> filters);
    List<PdfSearchEntry> pageByQuery(String keyword, Map<String, String> filters, int pageIndex, int pageSize, Map<String, String> sorting);
}
