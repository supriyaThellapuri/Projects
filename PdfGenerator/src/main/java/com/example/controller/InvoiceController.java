package com.example.controller;

import com.example.dto.InvoiceRequest;
import com.example.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
public class InvoiceController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody InvoiceRequest invoiceRequest) throws Exception {
        // Generate the PDF
        ByteArrayOutputStream pdfOutputStream = pdfService.generatePdf(invoiceRequest);

        // Return PDF as response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfOutputStream.toByteArray());
    }
}
