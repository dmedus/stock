package com.stock.entidades.servicio;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal; // Added import
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.ListaPrecio;
import com.stock.entidades.PrecioVino;
import com.stock.entidades.Vino; // Added import
import com.stock.repositorio.PrecioVinoRepository;

@Service
public class PrecioVinoServiceImpl implements PrecioVinoService {

    @Autowired
    private PrecioVinoRepository precioVinoRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal findPrecioByVino(Vino vino) {
        // This is a simplified implementation. In a real scenario, you would
        // likely fetch the price based on a specific ListaPrecio or other criteria.
        // For now, we'll just return the price of the first PrecioVino found for the given Vino.
        return precioVinoRepository.findByVino(vino)
                                   .stream()
                                   .findFirst()
                                   .map(PrecioVino::getPrecio)
                                   .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal findPrecioByVinoAndListaPrecio(Vino vino, ListaPrecio listaPrecio) {
        PrecioVino precioVino = precioVinoRepository.findByVinoAndListaPrecio(vino, listaPrecio);
        if (precioVino != null) {
            return precioVino.getPrecio();
        }
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioVino> findAll() {
        return precioVinoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PrecioVino findById(Long id) {
        return precioVinoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(PrecioVino precioVino) {
        precioVinoRepository.save(precioVino);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        precioVinoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void importarPreciosDesdeExcel(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0); // Asumimos que los datos están en la primera hoja

            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Saltar la fila de encabezado
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Long idPrecioVino = null;
                BigDecimal nuevoPrecio = null;

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0: // Columna ID
                            idPrecioVino = (long) currentCell.getNumericCellValue();
                            break;
                        case 3: // Columna Precio
                            nuevoPrecio = BigDecimal.valueOf(currentCell.getNumericCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }

                if (idPrecioVino != null && nuevoPrecio != null) {
                    PrecioVino precioVinoExistente = precioVinoRepository.findById(idPrecioVino).orElse(null);
                    if (precioVinoExistente != null) {
                        precioVinoExistente.setPrecio(nuevoPrecio);
                        precioVinoRepository.save(precioVinoExistente);
                    }
                }
            }

            workbook.close();

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioVino> findByListaPrecio(ListaPrecio listaPrecio) {
        return precioVinoRepository.findByListaPrecio(listaPrecio);
    }
}

