package com.mealManage.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfHeaderFooter  extends PdfPageEventHelper { 
	
	public static final  Font generalFont=FontFactory.getFont(FontFactory.HELVETICA, 8);
    
    @Override
    public void onStartPage(PdfWriter writer, Document document) { 
    }     
    
    /** 
     * Adds the header and the footer. 
     */ 
    @Override
    public void onEndPage(PdfWriter writer, Document document) { 
    	 final int currentPageNumber = writer.getCurrentPageNumber();
    		/*PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase(String.format("Page %d", writer.getPageNumber()), generalFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);
            table.addCell(cell);*/
            try {
				//document.add(table);
            	 final Rectangle pageSize = document.getPageSize();
                 final PdfContentByte directContent = writer.getDirectContent();
                 directContent.beginText();
                 directContent.setFontAndSize(BaseFont.createFont(), 8);
                 directContent.setTextMatrix(pageSize.getRight(40), pageSize.getBottom(20));
                 directContent.showText("Page "+String.valueOf(currentPageNumber));
                 directContent.endText();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
} 

