package MTISW.AutoFix1.Controllers;

import MTISW.AutoFix1.Entities.VehiculoEntity;
import MTISW.AutoFix1.Services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/registros")
    public ResponseEntity<List<VehiculoEntity>> registros()
    {
        if(vehiculoService.obtenerTodos() == null)
        {
            return ResponseEntity.ok(vehiculoService.obtenerTodos());
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}
