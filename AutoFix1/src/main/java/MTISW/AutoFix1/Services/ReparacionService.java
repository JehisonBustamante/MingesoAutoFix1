package MTISW.AutoFix1.Services;

import MTISW.AutoFix1.Entities.ReparacionEntity;
import MTISW.AutoFix1.Entities.VehiculoEntity;
import MTISW.AutoFix1.Repositories.ReparacionRepository;
import MTISW.AutoFix1.Repositories.VehiculoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReparacionService {
    @Autowired
    private ReparacionRepository reparacionRepository;

    private final VehiculoService vehiculoService;

    public ReparacionService(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    public List<ReparacionEntity> todos() {
        return reparacionRepository.findAll();
    }

    public void agregarNuevo(ReparacionEntity reparacionEntity) {
        reparacionEntity.setMontoReparacion(precioReparacion(reparacionEntity));
        reparacionRepository.save(reparacionEntity);
    }

    public Integer precioReparacion(ReparacionEntity reparacionEntity) {
        return switch (reparacionEntity.getTipoReparacion()) {
            case "Gasolina" -> reparacionGasolina(reparacionEntity.getReparacionNum());
            case "Diesel" -> reparacionDiesel(reparacionEntity.getReparacionNum());
            case "Hibrido" -> reparacionHibrido(reparacionEntity.getReparacionNum());
            case "Electrico" -> reparacionElectrico(reparacionEntity.getReparacionNum());
            default -> 0;
        };
    }

    public Integer reparacionGasolina(Integer numero) {
        return switch (numero) {
            case 1 -> 120000;
            case 2, 10 -> 130000;
            case 3 -> 350000;
            case 4 -> 210000;
            case 5, 9 -> 150000;
            case 6, 7 -> 100000;
            case 8 -> 180000;
            case 11 -> 80000;
            default -> 0;
        };

    }

    public Integer reparacionDiesel(Integer numero) {
        return switch (numero) {
            case 1, 6 -> 120000;
            case 2 -> 130000;
            case 3 -> 450000;
            case 4 -> 210000;
            case 5, 9 -> 150000;
            case 7 -> 100000;
            case 8 -> 180000;
            case 10 -> 140000;
            case 11 -> 80000;
            default -> 0;
        };
    }

    public Integer reparacionHibrido(Integer numero) {
        return switch (numero) {
            case 1, 9 -> 180000;
            case 2 -> 190000;
            case 3 -> 700000;
            case 4 -> 300000;
            case 5 -> 200000;
            case 6 -> 450000;
            case 7 -> 100000;
            case 8 -> 210000;
            case 10 -> 220000;
            case 11 -> 80000;
            default -> 0;
        };
    }

    public Integer reparacionElectrico(Integer numero) {
        return switch (numero) {
            case 1 -> 220000;
            case 2 -> 230000;
            case 3 -> 800000;
            case 4 -> 300000;
            case 5, 8 -> 250000;
            case 7 -> 100000;
            case 9 -> 180000;
            case 11 -> 80000;
            default -> 0;
        };
    }

    ////////////////////////////////////// COSTO TOTAL ////////////////////////////////////////

    //Calculo del costo completo
    public Double costoTotal(Integer idVehiculo) {
        double suma;
        ReparacionEntity reparacionEntity = reparacionRepository.getReferenceById(idVehiculo);
        suma = montoTotal(reparacionEntity.getIdVehiculo()).doubleValue();
        double bonos = descuentoBonos(idVehiculo).doubleValue();
        return ((suma + recargoTotal(reparacionEntity) - descuentoTotal(reparacionEntity)) - bonos)*1.19;}


    //Calculo del monto total de las reparaciones
    public Integer montoTotal(Integer idVehiculo) {
        Integer monto = 0;
        List<ReparacionEntity> listaReparaciones = reparacionRepository.findAllByIdVehiculo(idVehiculo);
        if(listaReparaciones.isEmpty()) {
            return 0;}
        else{
            for (ReparacionEntity listaReparacione : listaReparaciones) {
                monto = monto + listaReparacione.getMontoReparacion();}}
        return monto;}

    ////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// RECARGOS ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //Calculo del porcentaje total de recargo que se aplicará
    public Double recargoTotal(ReparacionEntity reparacionEntity)
    {
        VehiculoEntity vehiculoEntity = vehiculoService.obtenerPorID(reparacionEntity.getIdVehiculo());
        Double kilometraje = recargoKilometraje(vehiculoEntity.getTipo(), vehiculoEntity.getKilometraje());
        Double antiguedad = recargoAntiguedad(vehiculoEntity.getTipo(), vehiculoEntity.getAnio());
        Double atrasos = recargoAtraso(reparacionEntity.getFechaSalida(), reparacionEntity.getFechaClienteVienePorVehiculo());
        return (kilometraje + atrasos + antiguedad);
    }

    //Método para realizar el recargo de kilometraje a considerar en el recargo total
    public Double recargoKilometraje(String tipoVehiculo, Integer kilometraje) {
        if(Objects.equals(tipoVehiculo, "Sedan") || Objects.equals(tipoVehiculo, "Hatchback"))
        {
            return kilom1(kilometraje);
        }
        else if(Objects.equals(tipoVehiculo, "SUV") || Objects.equals(tipoVehiculo, "Pickup") || Objects.equals(tipoVehiculo, "Furgoneta"))
        {
            return kilom2(kilometraje);
        }
        else
        {
            return 0.00;
        }
    }

    //Recargo de kilometraje para los primeros 2 tipos de autos
    public Double kilom1(Integer kilometraje)
    {
        if(kilometraje<=5000)
            return 0.00;
        else if(kilometraje<=12000)
        {
            return 0.03;
        }
        else if(kilometraje<=25000)
        {
            return 0.07;
        }
        else if(kilometraje<=40000)
        {
            return 0.12;
        }
        else {
            return 0.20;
        }
    }

    //Recargo de kilometraje para los ultimos 3 tipos de autos
    public Double kilom2(Integer kilometraje)
    {
        if(kilometraje<=5000)
            return 0.00;
        else if(kilometraje<=12000)
        {
            return 0.05;
        }
        else if(kilometraje<=25000)
        {
            return 0.09;
        }
        else if(kilometraje<=40000)
        {
            return 0.12;
        }
        else {
            return 0.20;
        }
    }

    //Calculando recargo por antigüedad de autos
    public Double recargoAntiguedad(String tipoVehiculo, Integer anioFabricacion)
    {

        if(Objects.equals(tipoVehiculo, "Sedan") || Objects.equals(tipoVehiculo, "Hatchback"))
        {
            return antig1(anioFabricacion);
        }
        else if(Objects.equals(tipoVehiculo, "SUV") || Objects.equals(tipoVehiculo, "Pickup") || Objects.equals(tipoVehiculo, "Furgoneta"))
        {
            return antig2(anioFabricacion);
        }
        else
        {
            return 0.00;
        }
    }

    //Recargo de los primeros 2 tipos de autos en antigüedad
    public Double antig1(Integer anioFab)
    {
        Integer esteAnio = LocalDate.now().getYear();
        int antiguedad = esteAnio - anioFab;
        if(antiguedad<=5)
            return 0.00;
        else if(antiguedad <= 10)
        {
            return 0.05;
        }
        else if(antiguedad <= 15)
        {
            return 0.09;
        }
        else {
            return 0.15;
        }
    }

    //Recargo de los ultimos 3 tipos de autos en antigüedad
    public Double antig2(Integer anioFab)
    {
        Integer esteAnio = LocalDate.now().getYear();
        int antiguedad = esteAnio - anioFab;
        if(antiguedad<=5)
            return 0.00;
        else if(antiguedad <= 10)
        {
            return 0.07;
        }
        else if(antiguedad <= 15)
        {
            return 0.11;
        }
        else {
            return 0.20;
        }
    }

    //Recargo por atraso
    public Double recargoAtraso(LocalDate fechaSalida, LocalDate fechaRecogida)
    {
        if(fechaSalida.getYear() == fechaRecogida.getYear())
        {
            int salida = fechaSalida.getDayOfYear();
            int recogida = fechaRecogida.getDayOfYear();
            int diasDeAtraso = recogida - salida;
            return (diasDeAtraso*0.5);
        }
        else
        {
            int diffAnios = fechaRecogida.getYear() - fechaSalida.getYear();
            int salida = fechaSalida.getDayOfYear();
            int recogida = fechaRecogida.getDayOfYear();
            int diasDeAtraso = (365*diffAnios + recogida) - salida;
            return (diasDeAtraso*0.5);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// DESCUENTOS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //
    public Double descuentoTotal(ReparacionEntity reparacionEntity)
    {
        Double reparaciones = descuentoDeReparaciones(reparacionEntity.getIdVehiculo(), reparacionEntity.getTipoReparacion());
        Double diaAtencion = descuentoDiaAtencion(reparacionEntity.getFechaReparacion(), reparacionEntity.getHoraReparacion());
        return (reparaciones + diaAtencion);
    }

    //El descuento completo de las reparaciones
    public Double descuentoDeReparaciones(Integer idVehiculo, String tipoReparacion)
    {
        Integer reparacionesTotal = contarReparaciones(idVehiculo);
        return switch (tipoReparacion) {
            case "Gasolina" -> descuentoGasolina(reparacionesTotal);
            case "Diesel" -> descuentoDiesel(reparacionesTotal);
            case "Hibrido" -> descuentoHibrido(reparacionesTotal);
            case "Electrico" -> descuentoElectrico(reparacionesTotal);
            default -> 0.00;
        };
    }

    //Método auxiliar para contar las reparaciones en funcion de un vehiculo
    public Integer contarReparaciones(Integer idVehiculo)
    {
        List<ReparacionEntity> todasReparaciones = reparacionRepository.findAllByIdVehiculo(idVehiculo);
        Integer contador = 0;
        for(int i=0; i<todasReparaciones.size();i++)
        {
            contador++;
        }
        return contador;
    }


    //Descuento por gasolina
    public Double descuentoGasolina(Integer numeroReparaciones)
    {
        if(numeroReparaciones<=2){
            return 0.05;}
        else if(numeroReparaciones<=5) {
            return 0.10;}
        else if(numeroReparaciones<=9) {
            return 0.15;}
        else {
            return 0.20;}
    }


    //Descuento por diesel
    public Double descuentoDiesel(Integer numeroReparaciones)
    {
        if(numeroReparaciones<=2){
            return 0.07;}
        else if(numeroReparaciones<=5) {
            return 0.12;}
        else if(numeroReparaciones<=9) {
            return 0.17;}
        else {
            return 0.22;}
    }

    //Descuento hibrido
    public Double descuentoHibrido(Integer numeroReparaciones)
    {
        if(numeroReparaciones<=2){
            return 0.10;}
        else if(numeroReparaciones<=5) {
            return 0.15;}
        else if(numeroReparaciones<=9) {
            return 0.20;}
        else {
            return 0.25;}
    }

    //Descuento electrico
    public Double descuentoElectrico(Integer numeroReparaciones)
    {
        if(numeroReparaciones<=2){
            return 0.08;}
        else if(numeroReparaciones<=5) {
            return 0.13;}
        else if(numeroReparaciones<=9) {
            return 0.18;}
        else {
            return 0.23;}
    }

    // Método que realiza descuentos en funcion de el dia y la hora de atencion
    public Double descuentoDiaAtencion(LocalDate fechaIngreso, LocalTime horaIngreso)
    {
        if(fechaIngreso.getDayOfWeek() == DayOfWeek.MONDAY || fechaIngreso.getDayOfWeek() == DayOfWeek.THURSDAY)
        {
            //Se indica que sea menor o igual a 11 debido a que al ser igual a 11 abarca todos los minutos hasta 11:59
            if(horaIngreso.getHour() >= 9 && horaIngreso.getHour() <= 11)
            {
                return 0.10;
            }
        }
        else {
            return 0.00;
        }
        return 0.00;
    }

    // Se utiliza el ejemplo para descuento por bonos
    public Integer descuentoBonos(Integer idVehiculo)
    {
        VehiculoEntity vehiculoEntity = vehiculoService.obtenerPorID(idVehiculo);
        String marca = vehiculoEntity.getMarca();
        return switch (marca) {
            case "Toyota" -> 70000;
            case "Ford" -> 50000;
            case "Huyndai" -> 30000;
            case "Honda" -> 40000;
            default -> 0;
        };
    }


    // CALCULANDO R1 //

    public List<Double> reporteValoresFormula(Integer idVehiculo)
    {
        ReparacionEntity reparacionEntity = reparacionRepository.getReferenceById(idVehiculo);
        Double recargos = recargoTotal(reparacionEntity);
        Double descuentos = descuentoTotal(reparacionEntity);
        //OBTENER TODAS LAS REPARACIONES ASOCIADAS AL VEHICULO
        Double monto = montoTotal(idVehiculo).doubleValue();
        List<Double> valoresPorVehiculo = new ArrayList<>();
        valoresPorVehiculo.add(recargos);
        valoresPorVehiculo.add(descuentos);
        valoresPorVehiculo.add(monto);
        return valoresPorVehiculo;
    }


    // CALCULANDO R2 //

    //Metodo que calcula R2
    public List<List<?>> reporteReparacionesMontoTotal() {
        List<List<?>> reporte = new ArrayList<>();
        List<Integer> listaNumReparacion = new ArrayList<>();
        List<Integer> listaNumTiposPorReparacion = new ArrayList<>();
        List<Double> montosTotales = new ArrayList<>();
        for(int i=1; i<=11;i++) {
            List<ReparacionEntity> reparacionEntities = reparacionRepository.findAllByReparacionNum(i);
            listaNumReparacion.add(i);
            listaNumTiposPorReparacion.add(TiposDeVehiculosReparados(reparacionEntities));
            montosTotales.add(todosMontos(reparacionEntities));}
        reporte.add(listaNumReparacion);
        reporte.add(listaNumTiposPorReparacion);
        reporte.add(montosTotales);
        return reporte;}

    //Metodo para calcular el monto total de las reparaciones
    public Double todosMontos(List<ReparacionEntity> reparacionEntities)
    {
        double montos = 0.00;
        for (ReparacionEntity reparacionEntity : reparacionEntities) {
            montos = montos + reparacionEntity.getMontoReparacion().doubleValue();
        }
        return montos;
    }

    //Metodo para recuperar una lista de los tipos de vehiculos presentes en una lista de reparaciones (no se repiten)
    public Integer TiposDeVehiculosReparados(List<ReparacionEntity> reparacionEntities)
    {
        List<String> tipoVehiculos = new ArrayList<>();
        for (ReparacionEntity reparacionEntity : reparacionEntities) {
            VehiculoEntity vehiculoEntity = vehiculoService.obtenerPorID(reparacionEntity.getIdVehiculo());
            tipoVehiculos.add(vehiculoEntity.getMotor());
        }
        Set<String> tiposUnicosVehiculos = new HashSet<>(tipoVehiculos);
        return new ArrayList<>(tiposUnicosVehiculos).size();
    }




    // CALCULANDO R3 //

    // Método que devuelve una lista de promedios en función de una lista de marcas
    //Ejemplo:
    //Entrada:  [Toyota, Huyndai, Ford]
    //Salida: [20.00, 24.50, 29] DIAS PROMEDIO DE DEMORA DE REPARACION
    public List<Double> promedioPeriodosPorMarca(List<String> marca)
    {
        List<Double> promedios = new ArrayList<>();
        List<List<VehiculoEntity>> vehiculosPorMarca = new ArrayList<>();
        for (String s : marca) {
            vehiculosPorMarca.add(vehiculoService.todasPorMarca(s));
        }
        for (List<VehiculoEntity> vehiculoEntities : vehiculosPorMarca) {
            promedios.add(promedioPeriodos(vehiculoEntities));
        }
        return promedios;
    }

    // Método que calcula el promedio de los periodos (en dias) de las reparaciones asociadas a un grupo de vehiculos
    public Double promedioPeriodos(List<VehiculoEntity> vehiculoEntities)
    {
        double promedios = 0.00;
        int contador=0;
        for (VehiculoEntity vehiculoEntity : vehiculoEntities) {
            List<ReparacionEntity> reparacionesPorVehiculo = reparacionRepository.findAllByIdVehiculo(vehiculoEntity.getId());
            List<Period> periodosPorVehiculo = listaPeriodos(reparacionesPorVehiculo);
            // Convertir el periodo a periodo en numero de dias
            int totalDias = periodosPorVehiculo.stream()
                    .mapToInt(p -> p.getYears() * 365 + p.getMonths() * 30 + p.getDays())
                    .sum();
            promedios = promedios + ((double) totalDias / periodosPorVehiculo.size());
            contador++;
        }
        return (promedios/contador);
    }

    //Método que entrega una lista de periodos
    public List<Period> listaPeriodos(List<ReparacionEntity> reparacionEntities)
    {
        List<Period> periodos = new ArrayList<>();
        for (ReparacionEntity reparacionEntity : reparacionEntities) {
            periodos.add(periodo(reparacionEntity.getFechaReparacion(), reparacionEntity.getFechaSalida()));
        }
        return periodos;
    }
    //Método que calcula el periodo entre una fecha y otra
    public Period periodo(LocalDate fechaInicio, LocalDate fechaFin)
    {
        return Period.between(fechaInicio, fechaFin);
    }



    // CALCULANDO R4 //
    //Metodo que ingresa todos los datos para el reporte, con 9 listas contenidas en el reporte
    public List<List<?>> reporteReparacionesMotorMonto() {
        List<List<?>> reporte = new ArrayList<>();
        List<Integer> listaNumReparacion = new ArrayList<>();
        List<Integer> listaGasolina = new ArrayList<>();
        List<Integer> listaDiesel = new ArrayList<>();
        List<Integer> listaHibrido = new ArrayList<>();
        List<Integer> listaElectrico = new ArrayList<>();
        List<Double> montoGasolina = new ArrayList<>();
        List<Double> montoDiesel = new ArrayList<>();
        List<Double> montoHibrido = new ArrayList<>();
        List<Double> montoElectrico = new ArrayList<>();
        for(int i=1; i<=11;i++) {
            List<ReparacionEntity> reparacionEntities = reparacionRepository.findAllByReparacionNum(i);
            listaNumReparacion.add(i);
            listaGasolina.add(reportesPorMotor(reparacionEntities, "Gasolina").size());
            listaDiesel.add(reportesPorMotor(reparacionEntities, "Diesel").size());
            listaHibrido.add(reportesPorMotor(reparacionEntities, "Hibrido").size());
            listaElectrico.add(reportesPorMotor(reparacionEntities, "Electrico").size());
            montoGasolina.add(todosMontos(reportesPorMotor(reparacionEntities, "Gasolina")));
            montoDiesel.add(todosMontos(reportesPorMotor(reparacionEntities, "Diesel")));
            montoHibrido.add(todosMontos(reportesPorMotor(reparacionEntities, "Hibrido")));
            montoElectrico.add(todosMontos(reportesPorMotor(reparacionEntities, "Electrico")));}
        reporte.add(listaNumReparacion);
        reporte.add(listaGasolina);
        reporte.add(montoGasolina);
        reporte.add(listaDiesel);
        reporte.add(montoDiesel);
        reporte.add(listaHibrido);
        reporte.add(montoHibrido);
        reporte.add(listaElectrico);
        reporte.add(montoElectrico);
        return reporte;}

    //Método que filtra la lista de reparaciones en funcion del tipo de reparacion
    public List<ReparacionEntity> reportesPorMotor(List<ReparacionEntity> reparacionEntities, String motor)
    {
        return reparacionEntities.stream()
                .filter(reparacionEntity -> motor.equals(reparacionEntity.getTipoReparacion()))
                .collect(Collectors.toList());
    }

}
