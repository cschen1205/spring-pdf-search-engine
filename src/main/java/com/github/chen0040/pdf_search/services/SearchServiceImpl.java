package com.github.chen0040.pdf_search.services;

import com.github.chen0040.pdf_search.entities.PdfMediaEntity;
import com.github.chen0040.pdf_search.models.PdfSearchEntry;
import com.github.chen0040.pdf_search.utils.StringUtils;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SearchServiceImpl implements SearchService {

    private static final String PDF_INDEX = "pdf";
    private static final String PDF_TYPE = "pdf";

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private PdfMediaService pdfMediaService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    @Override
    public void indexAsync(String documentId, String username) {
        executorService.submit(() -> {
            PdfMediaEntity entity = pdfMediaService.findByDocumentId(documentId);

            PdfSearchEntry searchEntry = new PdfSearchEntry();
            searchEntry.setDocumentId(documentId);
            searchEntry.setUsername(username);
            searchEntry.setUpdatedAt(entity.getUpdatedAt().getTime());
            searchEntry.setCreatedAt(entity.getCreatedAt().getTime());
            searchEntry.setContent(getPdfContent(entity));

            try {
                elasticSearchService.index(searchEntry, PDF_INDEX, PDF_TYPE, documentId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String getPdfContent(PdfMediaEntity entity) {
        byte[] bytes = entity.getModel();
        PDDocument document = new PDDocument();
        try {
            document.load(bytes);

            String text = new PDFTextStripper().getText(document);
            logger.info("pdf extracted text: {}", text);
            return text;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private BoolQueryBuilder queryBuilder(String keyword, Map<String, String> filters) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        for(Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!StringUtils.isEmpty(value)) {
                if(key.equals("username")) {
                    boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery(key, value));
                } else if(key.equals("updatedAt") || key.equals("createdAt")){
                    String[] parts = value.split(",");
                    if(parts.length == 2) {

                        long startTime = StringUtils.parseLong(parts[0], 0L);
                        long endTime = StringUtils.parseLong(parts[1], new Date().getTime());

                        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.rangeQuery(key).from(startTime).to(endTime));
                    }
                }
            }
        }

        if (!StringUtils.isEmpty(keyword)) {
            boolQueryBuilder = boolQueryBuilder.filter(QueryBuilders.matchQuery("content", keyword).fuzziness(Fuzziness.AUTO));
        }

        return boolQueryBuilder;
    }

    @Override
    public long countByQuery(String keyword, Map<String, String> filters) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = queryBuilder(keyword, filters);
        sourceBuilder = sourceBuilder.query(boolQueryBuilder);
        sourceBuilder = sourceBuilder.size(1);
        sourceBuilder = sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        return countEntries(sourceBuilder);
    }

    private Long countEntries(SearchSourceBuilder sourceBuilder) {
        try {
            return elasticSearchService.count(PDF_INDEX, PDF_TYPE, sourceBuilder, PdfSearchEntry.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PdfSearchEntry> pageByQuery(String keyword, Map<String, String> filters, int pageIndex, int pageSize, Map<String, String> sorting) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = queryBuilder(keyword, filters);
        sourceBuilder = sourceBuilder.query(boolQueryBuilder);
        sourceBuilder = sourceBuilder.from(pageIndex * pageSize).size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        sourceBuilder = applySorting(sourceBuilder, sorting);

        return searchEntries(sourceBuilder);
    }

    private List<PdfSearchEntry> searchEntries(SearchSourceBuilder sourceBuilder) {
        try {
            return elasticSearchService.search(PDF_INDEX, PDF_TYPE, sourceBuilder, PdfSearchEntry.class);
        } catch (IOException ex) {
            logger.error("Failed to query elastic search to find the products", ex);
            return null;
        }
    }


    private SearchSourceBuilder applySorting(SearchSourceBuilder sourceBuilder, Map<String, String> sorting) {
        if(sorting != null) {
            for (Map.Entry<String, String> sortEntry : sorting.entrySet()) {
                String fieldName = sortEntry.getKey();
                String sortOrder = sortEntry.getValue();
                sourceBuilder = sourceBuilder.sort(fieldName, sortOrder.equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
            }
        }
        return sourceBuilder;
    }
}
