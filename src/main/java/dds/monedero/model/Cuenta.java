package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Cuenta {

  private BigDecimal saldo = 0; //Code smell, ya esta en el constructor
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(BigDecimal montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void depositar(BigDecimal cantidadDepositada) { //Poca expresividad poner por realizarDeposito cuanto por cantidad
    validarMonto(cantidadDepositada);
    validarCantidadDeMovimientos();
    agregarMovimiento(LocalDate.now(), cantidadDepositada, true );

  }

  public void extraer(BigDecimal cantidadExtraida) {
    validarMonto(cantidadExtraida);
    validarSaldoMenor(cantidadExtraida);
    validarExtraccionesDiarias(cantidadExtraida);
    agregarMovimiento(LocalDate.now(),cantidadExtraida, false );
  }



  public void agregarMovimiento(LocalDate fecha, BigDecimal cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
    if(esDeposito){
      setSaldo(saldo.add(cuanto));
    } else {
      setSaldo(saldo.subtract(cuanto));
    }
  }

//Validaciones
  public void validarMonto(BigDecimal unMonto ){
    if(unMonto.compareTo(BigDecimal.valueOf(0)) != 1) { //Significa que es negativo o 0
      throw new MontoNegativoException(unMonto + ": el monto a ingresar debe ser un valor positivo");
    }
  }
  public void validarCantidadDeMovimientos(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void validarSaldoMenor(BigDecimal cantidadExtraida){
    if (getSaldo().compareTo(cantidadExtraida) == -1) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void validarExtraccionesDiarias(BigDecimal cantidadExtraida) {
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = BigDecimal.valueOf(1000).subtract(montoExtraidoHoy);
    if (cantidadExtraida.compareTo(limite) == 1 ) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    return extraccionesA(fecha)
        .map(Movimiento::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Stream<Movimiento> extraccionesA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha));
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

}
