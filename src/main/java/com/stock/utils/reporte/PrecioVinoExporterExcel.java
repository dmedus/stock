package com.stock.utils.reporte;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.stock.entidades.PrecioVino;

public class PrecioVinoExporterExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<PrecioVino> listaPreciosVino;

    public PrecioVinoExporterExcel(List<PrecioVino> listaPreciosVino) {
        this.listaPreciosVino = listaPreciosVino;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("PreciosVino");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        Cell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("Vino");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("Lista de Precio");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("Precio");
        cell.setCellStyle(style);
    }

    private void writeDataRows() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (PrecioVino precioVino : listaPreciosVino) {
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(0);
            cell.setCellValue(precioVino.getId());

            cell = row.createCell(1);
            cell.setCellValue(precioVino.getVino().getNombre());

            cell = row.createCell(2);
            cell.setCellValue(precioVino.getListaPrecio().getNombre());

            cell = row.createCell(3);
            cell.setCellValue(precioVino.getPrecio().doubleValue());
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
