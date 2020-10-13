package de.monticore.featurediagram;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import mcfdtool.analyses.AllProducts;
import mcfdtool.analyses.NumberOfProducts;
import org.junit.Test;

import java.util.List;

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
    assertEquals(Integer.valueOf(2), result);
  }

  @Test
  public void testConditional2() {
    List<ASTFeatureConfiguration> i = new AllProducts().perform(getFD("fdvalid/ctc/Conditional2.fd"));

    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Conditional2.fd"));
    assertEquals(Integer.valueOf(5), result);
  }

  @Test
  public void testEquals() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Equals.fd"));
    assertEquals(Integer.valueOf(1), result);
  }

  @Test
  public void testExcludes() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Excludes.fd"));
    assertEquals(Integer.valueOf(1), result);
  }
  @Test
  public void testNot() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Not.fd"));
    assertEquals(Integer.valueOf(1), result);
  }

  @Test
  public void testNot2() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Not2.fd"));
    assertEquals(Integer.valueOf(1), result);
  }

  @Test
  public void testNotEquals() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/NotEquals.fd"));
    assertEquals(Integer.valueOf(1), result);
  }

  @Test
  public void testOr() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Or.fd"));
    assertEquals(Integer.valueOf(2), result);
  }

  @Test
  public void testRequires() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/ctc/Requires.fd"));
    assertEquals(Integer.valueOf(1), result);
  }

}
