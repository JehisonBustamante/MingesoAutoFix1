package MTISW.AutoFix1.Controllers;

import MTISW.AutoFix1.Entities.ReparacionEntity;
import MTISW.AutoFix1.Services.ReparacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reparaciones")
public class ReparacionController {
    @Autowired
    private ReparacionService reparacionService;

    @GetMapping("/todas-reparaciones")
    public ResponseEntity<List<ReparacionEntity>> todasReparaciones()
    {
        return ResponseEntity.ok(reparacionService.todos());
    }
    @PostMapping("/nueva-reparacion")
    public ResponseEntity<String> nuevaRep(@RequestBody ReparacionEntity reparacionEntity)
    {
        reparacionService.agregarNuevo(reparacionEntity);
        return ResponseEntity.ok("Reparacion agregada con exito");
    }

    @GetMapping("/costo/{id}")
    public ResponseEntity<Double> formulaCosto(@PathVariable("id") Integer id)
    {
        return ResponseEntity.ok(reparacionService.costoTotal(id));
    }

    @GetMapping("/costo/valores/{id}")
    public ResponseEntity<List<Double>> porVehiculo(@PathVariable Integer id)
    {
        return ResponseEntity.ok(reparacionService.reporteValoresFormula(id));
    }




}
