/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._symboltable.FeatureDiagramArtifactScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;
import mcfdtool.FeatureModelAnalysisTool;
import mcfdtool.analyses.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class FeatureDiagramAnalysisTest extends AbstractTest {

  public static final String TEST_RES = "src/test/resources/";

  protected ASTFeatureDiagram getFD(String modelFile) {
    FeatureDiagramArtifactScope as = FeatureDiagramTool
        .createSymbolTable(TEST_RES + modelFile, new ModelPath());
    String modelName = modelFile.replace(".fd", "");
    if (modelName.contains("/")) {
      modelName = modelName.substring(modelFile.lastIndexOf("/")+1);
    }

    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol = as
        .resolveFeatureDiagram(modelName);
    assertTrue(optionalFeatureDiagramSymbol.isPresent());
    assertNotNull(optionalFeatureDiagramSymbol.get());
    assertNotNull(optionalFeatureDiagramSymbol.get().getAstNode());
    return optionalFeatureDiagramSymbol.get().getAstNode();
  }

  protected ASTFeatureConfiguration getFC(String modelFile) {
    FeatureConfigurationArtifactScope symbolTable = FeatureConfigurationTool
        .createSymbolTable(TEST_RES + modelFile, new ModelPath(Paths.get(TEST_RES)));

    String modelName = modelFile.replace(".fc", "");
    if (modelName.contains("/")) {
      modelName = modelName.substring(modelFile.lastIndexOf("/")+1);
    }

    Optional<FeatureConfigurationSymbol> featureConfOpt = symbolTable
        .resolveFeatureConfiguration(modelName);
    assertTrue(featureConfOpt.isPresent());
    assertNotNull(featureConfOpt.get());
    return featureConfOpt.get().getAstNode();
  }

  protected <T> T performAnalysis(String fdModelFile, Analysis<T> analysis) {
    ASTFeatureDiagram featureDiagram = getFD(fdModelFile);
    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagram);
    modelAnalysisTool.addAnalysis(analysis);
    modelAnalysisTool.performAnalyses();
    assertTrue(analysis.getResult().isPresent());
    return analysis.getResult().get();
  }

  @Test
  public void testPhoneExample() {
    Integer result = performAnalysis("fdvalid/Phone.fd", new NumberOfProducts());
    assertEquals(new Integer(84), result);
  }

  @Test
  public void testPhoneComplexExample() {
    Integer result = performAnalysis("fdvalid/PhoneComplex.fd", new NumberOfProducts());
    assertEquals(new Integer(48), result);
  }

  @Test
  public void testDeadFeatures() {
    List<String> result = performAnalysis("DeadFeatures.fd", new DeadFeature());
    assertTrue(result.contains("B"));
    assertEquals(1, result.size());
  }

  @Test
  public void testFalseOptional() {
    List<String> result = performAnalysis("FalseOptional.fd", new FalseOptional());
    assertTrue(result.contains("B"));
    assertEquals(1, result.size());
  }

  @Test
  public void testVoid1() {
    Boolean result = performAnalysis("Void.fd", new IsVoidFeatureModel());
    assertTrue(result);
  }

  @Test
  public void testVoid2() {
    Boolean result = performAnalysis("DeadFeatures.fd", new IsVoidFeatureModel());
    assertFalse(result);
  }

  @Test
  public void testFilter1() {
    Set<ASTFeatureConfiguration> result = performAnalysis("FalseOptional.fd",
        new Filter(getFC("CompleteToValid.fc")));
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFilter2() {
    Set<ASTFeatureConfiguration> result = performAnalysis(
        "FalseOptional.fd", new Filter(getFC("ValidConfig.fc")));
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFilter3() {
    Set<ASTFeatureConfiguration> result = performAnalysis(
        "FalseOptional.fd", new Filter(getFC("InvalidConfig.fc")));
    assertFalse(result.isEmpty());
  }

  @Test
  public void testisValid1() {
    Boolean result = performAnalysis("FalseOptional.fd",
        new IsValid(getFC("CompleteToValid.fc")));
    assertFalse(result);
  }

  @Test
  public void testisValid2() {
    Boolean result = performAnalysis("FalseOptional.fd",
        new IsValid(getFC("ValidConfig.fc")));
    assertTrue(result);
  }

  @Test
  public void testisValid3() {
    Boolean result = performAnalysis("DeadFeatures.fd",
        new IsValid(getFC("InvalidConfig.fc")));
    assertFalse(result);
  }

  @Test
  public void testAllProducts() {
    Set<ASTFeatureConfiguration> result = performAnalysis("fdvalid/Phone.fd", new AllProducts());
    assertEquals(84, result.size());
  }

  @Test
  public void testFindValid1() {
    performAnalysis("fdvalid/Phone.fd", new FindValidConfig());
  }

  @Test
  public void testFindValid2() {
    ASTFeatureDiagram featureDiagram = getFD("Void.fd");
    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagram);
    FindValidConfig analysis = new FindValidConfig();
    modelAnalysisTool.addAnalysis(analysis);
    modelAnalysisTool.performAnalyses();
    assertFalse(analysis.getResult().isPresent());
  }

}
