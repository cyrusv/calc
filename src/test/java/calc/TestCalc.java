package calc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCalc {

  @Test
  public void testMultiply() {
    CalcResource calc = new CalcResource();
    assertEquals(45, calc.multiply(5, 9));
    assertEquals(0, calc.multiply(5, 0));
  }

  @Test
  public void testAdd() {
    CalcResource calc = new CalcResource();
    assertEquals(14, calc.add(5, 9));
    assertEquals(5, calc.add(5, 0));
  }

  @Test
  public void testDivide() {
    CalcResource calc = new CalcResource();
    assertEquals(3, calc.divide(13, 4));
  }
}
