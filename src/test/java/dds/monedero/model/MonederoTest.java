package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }


  @Test
  void Poner() {
    cuenta.depositar(BigDecimal.valueOf(1500));
    System.out.println(cuenta.getMovimientos().get(0).getMonto());
    assertEquals(BigDecimal.valueOf(1500), cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.depositar(BigDecimal.valueOf(-1500)));
  }

  @Test
  void TresDepositos() {
    cuenta.depositar(BigDecimal.valueOf(1500));
    cuenta.depositar(BigDecimal.valueOf(456));
    cuenta.depositar(BigDecimal.valueOf(1900));
    assertEquals(BigDecimal.valueOf(3856), cuenta.getSaldo());
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.depositar(BigDecimal.valueOf(1500));
          cuenta.depositar(BigDecimal.valueOf(456));
          cuenta.depositar(BigDecimal.valueOf(1900));
          cuenta.depositar(BigDecimal.valueOf(245));
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(BigDecimal.valueOf(90));
          cuenta.extraer(BigDecimal.valueOf(1001));
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(BigDecimal.valueOf(5000));
      cuenta.extraer(BigDecimal.valueOf(1001));
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraer(BigDecimal.valueOf(-500)));
  }

  @Test
  public void CrearCuentaConSaldo() {
    Cuenta cuentaConSaldo = new Cuenta(BigDecimal.valueOf(1500));
    assertEquals(BigDecimal.valueOf(1500), cuentaConSaldo.getSaldo() );
  }


}