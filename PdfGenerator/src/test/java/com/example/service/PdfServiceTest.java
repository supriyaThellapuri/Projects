//package com.example.service;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.assertj.core.util.Arrays;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.example.dto.InvoiceDto;
//import com.example.dto.ItemDto;
//
//@SpringBootTest
//public class PdfServiceTest {
//	
//	@Autowired
//	private PdfService pdfService;
//	
//	@Test
//	public void testGenerateOrGetInvoicePdf()throws Exception {
//		InvoiceDto request = new InvoiceDto();
//		request.setSeller("XYZ Pvt. Ltd.");
//		request.setSellerGstin("29AABBCCDD121ZD");
//		request.setSellerAddress("New Delhi, India");
//		request.setBuyer("Vedant Computers");
//		request.setBuyerGstin("29AABBCCDD131ZD");
//		request.setBuyerAddress("New Delhi, India");
//		
//		List<ItemDto> item1 = new ArrayList();
//		item1.add(new ItemDto("Product 1", "12 Nos", 123.00, 1476.00));
//		request.setItems(item1);
//		
//		byte[] pdfBytes = pdfService.generateOrGetInvoicePdf(request);
//		
//		assertNotNull(pdfBytes, "PDF should not be null");
//		
//		
//	}
//	
//
//}
