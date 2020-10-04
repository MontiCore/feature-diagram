package de.monticore.featurediagram;

import mcfdtool.analyses.NumberOfProducts;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CTCTest extends AbstractTest{
  
  @Test
  public void testAnd() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/And.fd"));
    assertEquals(Integer.valueOf(1), result);
  }
  
  @Test
  public void testBrackets() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Brackets.fd"));
    assertEquals(Integer.valueOf(5), result);
  }
  
  @Test
  public void testConditional() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Conditional.fd"));
    assertEquals(Integer.valueOf(1), result);
  }
}
