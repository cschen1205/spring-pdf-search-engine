package com.github.chen0040.pdf_search.controllers;

import com.github.chen0040.pdf_search.entities.PdfMediaEntity;
import com.github.chen0040.pdf_search.services.PdfMediaService;
import com.github.chen0040.pdf_search.utils.HttpResponseHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("Pdf Download")
@Controller
public class PdfController {

    @Autowired
    private PdfMediaService pdfMediaService;

    @RequestMapping(value="/api/download-pdf", method= RequestMethod.GET)
    public void downloadPdf(@RequestParam("documentId") String documentId, HttpServletResponse response){
        PdfMediaEntity entity = pdfMediaService.findByDocumentId(documentId);
        if(entity != null){
            try {
                HttpResponseHelper.sendBinaryData(response, entity.getModel());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HttpResponseHelper.sendDefaultPdf(response);
        }
    }
}
