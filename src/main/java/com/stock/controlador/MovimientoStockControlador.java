package com.stock.controlador;

import com.stock.entidades.Deposito;
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
import java.util.Map;

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

        try {
            stockService.moverStock(vino, depositoOrigen, depositoDestino, cantidad);
            flash.addFlashAttribute("success", "Movimiento de stock realizado con éxito.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientoStock";
    }

    @GetMapping("/ajusteStockMasivo")
    public String mostrarAjusteMasivo(Model model) {
        List<Vino> vinos = vinoService.findAll();
        Map<Long, Integer> stockMap = stockService.getStockTotalMap();
        model.addAttribute("vinos", vinos);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("titulo", "Ajuste Masivo de Stock");
        return "ajusteStockMasivo";
    }

    @PostMapping("/ajusteStockMasivo")
    public String guardarAjusteMasivo(@RequestParam Map<String, String> params, RedirectAttributes flash) {
        int actualizados = 0;
        StringBuilder errores = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().startsWith("stock_")) continue;
            try {
                Long vinoId = Long.parseLong(entry.getKey().replace("stock_", ""));
                Integer nuevaCantidad = Integer.parseInt(entry.getValue());
                if (nuevaCantidad < 0) continue;
                Vino vino = vinoService.findById(vinoId);
                if (vino != null) {
                    stockService.ajustarStock(vino, nuevaCantidad);
                    actualizados++;
                }
            } catch (NumberFormatException e) {
                // campo vacío o inválido, se ignora
            } catch (RuntimeException e) {
                errores.append(e.getMessage()).append(" ");
            }
        }

        if (errores.length() > 0) {
            flash.addFlashAttribute("error", errores.toString());
        } else {
            flash.addFlashAttribute("success", "Stock actualizado para " + actualizados + " vinos.");
        }
        return "redirect:/ajusteStockMasivo";
    }
}
