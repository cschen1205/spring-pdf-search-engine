package com.github.chen0040.pdf_search.viewmodels;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PdfSearchQuery {
    private String keyword = "";
    private Map<String, String> filters = new HashMap<>();
    private Map<String, String> sorting = new HashMap<>();
}
