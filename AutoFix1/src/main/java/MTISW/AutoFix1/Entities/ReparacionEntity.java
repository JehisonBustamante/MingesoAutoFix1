package MTISW.AutoFix1.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReparacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idReparacion;
    private Integer idVehiculo;
    private LocalDate fechaReparacion;
    private LocalTime horaReparacion;
    private String tipoReparacion; // GASOLINA, DIESEL, ELECTRICO O HIBRIDO
    private Integer reparacionNum; // 1 - 11
    private Integer montoReparacion; //EN PESOS CHILENOS
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private LocalDate fechaClienteVienePorVehiculo;
    private LocalTime horaClienteVienePorVehiculo;
}
