package com.github.chen0040.pdf_search.controllers;

import com.github.chen0040.pdf_search.services.PdfMediaService;
import com.github.chen0040.pdf_search.services.SearchService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Api("Pdf Upload")
@Controller
public class PdfUploadController {

    @Autowired
    private PdfMediaService pdfMediaService;

    @Autowired
    private SearchService searchService;

    private static final Logger logger = LoggerFactory.getLogger(PdfUploadController.class);

    @RequestMapping(value = "/api/upload-pdf", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> uploadPdf(@RequestParam("documentId") String documentId,
                                  @RequestParam("username") String username,
                                  @RequestParam("file") MultipartFile file)
            throws ServletException, IOException {

        Map<String, Object> result = new HashMap<>();

        try {
            byte[] bytes = file.getBytes();
            logger.info("pdf bytes received: {}", bytes.length);

            long id = pdfMediaService.savePdf(bytes, username, documentId);

            searchService.indexAsync(documentId, username);

            logger.info("saved pdf: {}", id);
            result.put("id", id);
            result.put("documentId", documentId);
            result.put("success", true);
            result.put("error", "");

            return result;
        }catch(IOException ex) {
            logger.error("Failed to process the uploaded image", ex);
            result.put("success", false);
            result.put("id", "");
            result.put("documentId", documentId);
            result.put("error", ex.getMessage());
            return result;
        }
    }
}
