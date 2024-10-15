package com.example.service;

import com.example.dto.InvoiceRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public ByteArrayOutputStream generatePdf(InvoiceRequest invoiceRequest) throws Exception {
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("invoice", invoiceRequest);

        // Process HTML template to string
        String html = templateEngine.process("invoiceTemplate", context);

        // Create PDF from HTML
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        // Convert HTML to PDF using XMLWorkerHelper
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(html.getBytes()));

        // Close the document
        document.close();

        return outputStream;
    }
}
