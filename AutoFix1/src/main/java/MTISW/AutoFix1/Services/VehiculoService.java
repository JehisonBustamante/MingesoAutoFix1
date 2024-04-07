package MTISW.AutoFix1.Services;

import MTISW.AutoFix1.Entities.VehiculoEntity;
import MTISW.AutoFix1.Repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;

    public List<VehiculoEntity> obtenerTodos()
    {
        return vehiculoRepository.findAll();
    }
}