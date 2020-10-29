/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import de.monticore.io.paths.ModelPath;
import mcfdtool.analyses.*;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureDiagramAnalysisTest extends AbstractTest {

  @Test
  public void testPhoneExample() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/Phone.fd"));
    assertEquals(new Integer(84), result);
  }

  @Test
  public void testPhoneComplexExample() {
    Integer result = new NumberOfProducts().perform(getFD("fdvalid/PhoneComplex.fd"));
    assertEquals(new Integer(48), result);
  }

  @Test
  public void testDeadFeatures() {
    List<String> result = new DeadFeature().perform(getFD("DeadFeatures.fd"));
    assertTrue(result.contains("B"));
    assertEquals(1, result.size());
  }

  @Test
  public void testFalseOptional() {
    List<String> result = new FalseOptional().perform(getFD("FalseOptional.fd"));
    assertTrue(result.contains("B"));
    assertEquals(1, result.size());
  }

  @Test
  public void testVoid1() {
    Boolean result = new IsVoidFeatureModel().perform(getFD("Void.fd"));
    assertTrue(result);
  }

  @Test
  public void testVoid2() {
    Boolean result = new IsVoidFeatureModel().perform(getFD("DeadFeatures.fd"));
    assertFalse(result);
  }

  @Test
  public void testCompleteToValid1() {
    ASTFeatureDiagram fd = getFD("FalseOptional.fd");
    ASTFeatureConfiguration fc = getFC("CompleteToValid.fc");
    ASTFeatureConfiguration result = new CompleteToValid().perform(fd, fc);
    assertFalse(null == result);
  }

  @Test
  public void testCompleteToValid2() {
    ASTFeatureDiagram fd = getFD("FalseOptional.fd");
    ASTFeatureConfiguration fc = getFC("ValidConfig.fc");
    ASTFeatureConfiguration result = new CompleteToValid().perform(fd, fc);
    assertFalse(null == result);
  }

  @Test
  public void testCompleteToValid3() {
    ASTFeatureDiagram fd = getFD("FalseOptional.fd");
    ASTFeatureConfiguration fc = getFC("InvalidConfig.fc");
    ASTFeatureConfiguration result = new CompleteToValid().perform(fd, fc);
    assertFalse(null == result);
  }

  @Test
  public void testisValid1() {
    ASTFeatureDiagram fd = getFD("FalseOptional.fd");
    ASTFeatureConfiguration fc = getFC("CompleteToValid.fc");
    Boolean result = new IsValid().perform(fd, fc);
    assertFalse(result);
  }

  @Test
  public void testisValid2() {
    ASTFeatureDiagram fd = getFD("FalseOptional.fd");
    ASTFeatureConfiguration fc = getFC("ValidConfig.fc");
    Boolean result = new IsValid().perform(fd, fc);
    assertTrue(result);
  }

  @Test
  public void testisValid3() {
    ASTFeatureDiagram fd = getFD("DeadFeatures.fd");
    ASTFeatureConfiguration fc = getFC("InvalidConfig.fc");
    Boolean result = new IsValid().perform(fd, fc);
    assertFalse(result);
  }

  @Test
  public void testAllProducts() {
    ASTFeatureDiagram fd = getFD("fdvalid/Phone.fd");
    List<ASTFeatureConfiguration> result = new AllProducts().perform(fd);
    assertEquals(84, result.size());
  }

  @Test
  public void testFindValid1() {
    ASTFeatureConfiguration result = new FindValid().perform(getFD("fdvalid/Phone.fd"));
    assertFalse(null == result);
  }

  @Test
  public void testFindValid2() {
    ASTFeatureConfiguration result = new FindValid().perform(getFD("Void.fd"));
    assertTrue(null == result);
  }

}
