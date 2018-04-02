package com.github.chen0040.pdf_search.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfSearchEntry {
    private String documentId;
    private String content;
    private String username;
    private long updatedAt;
    private long createdAt;
}
