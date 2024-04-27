package MTISW.AutoFix1.Controllers;

import MTISW.AutoFix1.Services.ReparacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reparaciones")
public class ReparacionController {
    @Autowired
    private ReparacionService reparacionService;
}
