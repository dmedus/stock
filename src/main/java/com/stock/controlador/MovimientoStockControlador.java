package com.stock.controlador;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import com.stock.entidades.servicio.DepositoService;
import com.stock.entidades.servicio.StockService;
import com.stock.entidades.servicio.VinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MovimientoStockControlador {

    @Autowired
    private VinoService vinoService;

    @Autowired
    private DepositoService depositoService;

    @Autowired
    private StockService stockService;

    @GetMapping("/movimientoStock")
    public String mostrarFormulario(Model model) {
        List<Vino> vinos = vinoService.findAll();
        List<Deposito> depositos = depositoService.findAll();
        model.addAttribute("vinos", vinos);
        model.addAttribute("depositos", depositos);
        return "movimientoStock";
    }

    @PostMapping("/movimientoStock")
    public String moverStock(@RequestParam Long vinoId,
                             @RequestParam Long depositoOrigenId,
                             @RequestParam Long depositoDestinoId,
                             @RequestParam Integer cantidad,
                             RedirectAttributes flash) {

        Vino vino = vinoService.findById(vinoId);
        Deposito depositoOrigen = depositoService.findById(depositoOrigenId);
        Deposito depositoDestino = depositoService.findById(depositoDestinoId);

        if (vino == null || depositoOrigen == null || depositoDestino == null) {
            flash.addFlashAttribute("error", "Vino o depósito no válido.");
            return "redirect:/movimientoStock";
        }

        if (depositoOrigen.getId().equals(depositoDestino.getId())) {
            flash.addFlashAttribute("error", "El depósito de origen y destino no pueden ser el mismo.");
            return "redirect:/movimientoStock";
        }

        Stock stockOrigen = stockService.findByVinoAndDeposito(vino, depositoOrigen);

        if (stockOrigen == null || stockOrigen.getCantidad() < cantidad) {
            flash.addFlashAttribute("error", "No hay suficiente stock en el depósito de origen.");
            return "redirect:/movimientoStock";
        }

        stockOrigen.setCantidad(stockOrigen.getCantidad() - cantidad);
        stockService.save(stockOrigen);

        Stock stockDestino = stockService.findByVinoAndDeposito(vino, depositoDestino);
        if (stockDestino == null) {
            stockDestino = new Stock();
            stockDestino.setVino(vino);
            stockDestino.setDeposito(depositoDestino);
            stockDestino.setCantidad(cantidad);
        } else {
            stockDestino.setCantidad(stockDestino.getCantidad() + cantidad);
        }
        stockService.save(stockDestino);

        flash.addFlashAttribute("success", "Movimiento de stock realizado con éxito.");
        return "redirect:/movimientoStock";
    }
}
