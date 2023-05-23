package com.stock.utils.reporte;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.stock.entidades.Stock;

public class StockExporterPDF {
	
	private List<Stock> listaStocks;

	public StockExporterPDF(List<Stock> listaStocks) {
		super();
		this.listaStocks = listaStocks;
	}
	
	private void escribirCabeceraDeLaTabla(PdfPTable tabla) {
		PdfPCell celda = new PdfPCell();
		celda.setBackgroundColor(Color.BLUE);
		celda.setPadding(5);
		
		Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
		fuente.setColor(Color.WHITE);
		
		celda.setPhrase(new Phrase("ID",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Modelo",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Cond",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("IMEI",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Proveedor",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Stock",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Fecha",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Lugar",fuente));
		tabla.addCell(celda);
		
		celda.setPhrase(new Phrase("Observacion",fuente));
		tabla.addCell(celda);
	}
	
	
	private void escribirDatosDeLaTabla(PdfPTable tabla) {
		for (Stock stock : listaStocks) {
			tabla.addCell(String.valueOf(stock.getId()));
			tabla.addCell(stock.getModelo().getNombre());
			tabla.addCell(stock.getCond());
			tabla.addCell(stock.getImei());
			tabla.addCell(stock.getProveedor());
			tabla.addCell(String.valueOf(stock.isInStock()));
			tabla.addCell(stock.getFecha().toString());
			tabla.addCell(stock.getLugar());
			tabla.addCell(stock.getObservacion());			
		}
		
	}
	
	public void exportar(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fuente.setColor(Color.BLUE);
		fuente.setSize(18);
		
		Paragraph titulo = new Paragraph("Lista Stock",fuente);
		document.add(titulo);
		
		PdfPTable tabla = new PdfPTable(9);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(15);
		
		tabla.setWidths(new float[] { 1f, 2.3f, 2.3f, 6f, 2.9f, 3.5f, 2f, 2.2f });
		tabla.setWidthPercentage(110);

		escribirCabeceraDeLaTabla(tabla);
		escribirDatosDeLaTabla(tabla);

		document.add(tabla);
		document.close();
		
	}
	

}
