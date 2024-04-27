package MTISW.AutoFix1.Repositories;

import MTISW.AutoFix1.Entities.ReparacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReparacionRepository extends JpaRepository<ReparacionEntity, Integer> {

    List<ReparacionEntity> findAllByIdVehiculo(Integer id);


}
