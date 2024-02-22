package com.stock.utils.reporte;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.stock.entidades.Stock;

public class StockExporterExcel {

	private XSSFWorkbook libro;
	private XSSFSheet hoja;

	private List<Stock> listaStock;

	public StockExporterExcel(List<Stock> listaStock) {
		this.listaStock = listaStock;
		libro = new XSSFWorkbook();
		hoja = libro.createSheet("Stock");
	}

	private void escribirCabeceraDeTabla() {
		Row fila = hoja.createRow(0);
		
		CellStyle estilo = libro.createCellStyle();
		XSSFFont fuente = libro.createFont();
		fuente.setBold(true);
		fuente.setFontHeight(16);
		estilo.setFont(fuente);
		
		Cell celda = fila.createCell(0);
		celda.setCellValue("ID");
		celda.setCellStyle(estilo);
		
		//terminar
		
		celda = fila.createCell(1);
		celda.setCellValue("Modelo");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(2);
		celda.setCellValue("Cond");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(3);
		celda.setCellValue("IMEI");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(4);
		celda.setCellValue("Proveedor");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(5);
		celda.setCellValue("Costo");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(6);
		celda.setCellValue("Tipo");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(7);
		celda.setCellValue("Stock");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(8);
		celda.setCellValue("Fecha");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(9);
		celda.setCellValue("Lugar");
		celda.setCellStyle(estilo);
		
		celda = fila.createCell(10);
		celda.setCellValue("Observacion");
		celda.setCellStyle(estilo);
	}
	
	private void escribirDatosDeLaTabla() {
		int nueroFilas = 1;
		
		CellStyle estilo = libro.createCellStyle();
		XSSFFont fuente = libro.createFont();
		fuente.setFontHeight(14);
		estilo.setFont(fuente);
		
		CellStyle cellStyleFecha = libro.createCellStyle();   
		CreationHelper createHelper = libro.getCreationHelper();
		cellStyleFecha.setDataFormat(
		    createHelper.createDataFormat().getFormat("m/d/yy"));
		cellStyleFecha.setFont(fuente);
		
		for(Stock stock : listaStock) {
			Row fila = hoja.createRow(nueroFilas ++);
			
			Cell celda = fila.createCell(0);
			celda.setCellValue(stock.getId());
			hoja.autoSizeColumn(0);
			celda.setCellStyle(estilo);
			
			//terminar
			celda = fila.createCell(1);
			String modeloCelular = stock.getModelo().getNombre() + " " +stock.getModelo().getCapacidad() + " " +stock.getModelo().getColor();  
			celda.setCellValue(modeloCelular);
			hoja.autoSizeColumn(1);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(2);
			celda.setCellValue(stock.getCond());
			hoja.autoSizeColumn(2);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(3);
			celda.setCellValue(stock.getImei());
			hoja.autoSizeColumn(3);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(4);
			celda.setCellValue(stock.getProveedor());
			hoja.autoSizeColumn(4);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(5);
			celda.setCellValue(stock.getCosto());
			hoja.autoSizeColumn(5);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(6);
			celda.setCellValue(stock.getTipoCosto());
			hoja.autoSizeColumn(6);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(7);
			celda.setCellValue(stock.isInStock());
			hoja.autoSizeColumn(7);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(8);
			celda.setCellValue(stock.getFecha());
			hoja.autoSizeColumn(8);
			celda.setCellStyle(cellStyleFecha);
			
			celda = fila.createCell(9);
			celda.setCellValue(stock.getLugar());
			hoja.autoSizeColumn(9);
			celda.setCellStyle(estilo);
			
			celda = fila.createCell(10);
			celda.setCellValue(stock.getObservacion());
			hoja.autoSizeColumn(10);
			celda.setCellStyle(estilo);
		}
	}
	
	public void exportar(HttpServletResponse response) throws IOException {
		escribirCabeceraDeTabla();
		escribirDatosDeLaTabla();
		
		ServletOutputStream outPutStream = response.getOutputStream();
		libro.write(outPutStream);
		
		libro.close();
		outPutStream.close();
	}
}
