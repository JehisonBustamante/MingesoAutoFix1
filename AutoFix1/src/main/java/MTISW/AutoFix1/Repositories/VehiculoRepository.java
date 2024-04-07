package MTISW.AutoFix1.Repositories;

import MTISW.AutoFix1.Entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<VehiculoEntity, Integer> {
}
