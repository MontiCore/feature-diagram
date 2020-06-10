/* (c) https://github.com/MontiCore/monticore */
package featurediagram;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.LogStub;
import featureconfiguration._ast.ASTFCCompilationUnit;
import featureconfiguration._parser.FeatureConfigurationParser;
import featureconfiguration._symboltable.*;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Before;
import org.junit.Test;
import tool.FeatureModelAnalysisTool;
import tool.analyses.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureDiagramAnalysisTest {

  @Before
  public void init(){
    LogStub.init();
  }

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile, ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureDiagramLanguage lang = new FeatureDiagramLanguage();
    FeatureDiagramGlobalScope globalScope = new FeatureDiagramGlobalScope(mp, lang);
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile)
      throws IOException {
    return setupSymbolTable(modelFile, new ModelPath());
  }

  protected FeatureDiagramSymbol setupFeatureDiagramm(FeatureDiagramArtifactScope scope, String name){
    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol =  scope.resolveFeatureDiagram(name);
    assertTrue(optionalFeatureDiagramSymbol.isPresent());
    return optionalFeatureDiagramSymbol.get();
  }

  protected FeatureConfigurationSymbol setupConfigSymTab(String modelFile, ModelPath modelPath, String name) throws IOException{
    ASTFCCompilationUnit ast = new FeatureConfigurationParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureConfigurationLanguage lang = new FeatureConfigurationLanguage();
    FeatureConfigurationGlobalScope globalScope = new FeatureConfigurationGlobalScope(modelPath, lang);
    FeatureConfigurationSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    Optional<FeatureConfigurationSymbol> featureConfOpt = symbolTable.createFromAST(ast).resolveFeatureConfiguration(name);
    assertTrue(featureConfOpt.isPresent());
    return featureConfOpt.get();
  }

  protected FeatureConfigurationSymbol setupConfigSymTab(String modelFile, String name) throws IOException{
    return setupConfigSymTab(modelFile, new ModelPath(), name);
  }

  @Test
  public void testPhoneExample() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
        "../fd-lang/src/test/resources/fdvalid/Phone.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Phone");

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    assertTrue(numberOfProducts.getResult().isPresent());
    assertEquals(new Integer(84), numberOfProducts.getResult().get());

  }

  @Test
  public void testPhoneComplexExample() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
        "../fd-lang/src/test/resources/fdvalid/PhoneComplex.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Phone");
    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    assertTrue(numberOfProducts.getResult().isPresent());
    assertEquals(new Integer(48), numberOfProducts.getResult().get());
  }

  @Test
  public void testDeadFeatures() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "src/test/resources/DeadFeatures.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "DeadFeatures");
    DeadFeature deadFeature = new DeadFeature();
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    tool.addAnalysis(deadFeature);
    tool.performAnalyses();
    assertTrue(deadFeature.getResult().isPresent());
    List<String> deadFeatures = deadFeature.getResult().get();

    assertTrue(deadFeatures.contains("B"));
    assertEquals(1, deadFeatures.size());
  }

  @Test
  public void testFalseOptional() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "src/test/resources/FalseOptional.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FalseOptional falseOptional = new FalseOptional();
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    tool.addAnalysis(falseOptional);
    tool.performAnalyses();
    assertTrue(falseOptional.getResult().isPresent());
    List<String> falseOpts = falseOptional.getResult().get();

    assertTrue(falseOpts.contains("B"));
    assertEquals(1, falseOpts.size());
  }
  @Test
  public void testVoid1() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "src/test/resources/Void.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Void");
    IsVoidFeatureModel voidFeatureModel = new IsVoidFeatureModel();
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    tool.addAnalysis(voidFeatureModel);
    tool.performAnalyses();
    assertTrue(voidFeatureModel.getResult().isPresent());
    assertTrue(voidFeatureModel.getResult().get());
  }

  @Test
  public void testVoid2() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "src/test/resources/DeadFeatures.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "DeadFeatures");
    IsVoidFeatureModel voidFeatureModel = new IsVoidFeatureModel();
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    tool.addAnalysis(voidFeatureModel);
    tool.performAnalyses();
    assertTrue(voidFeatureModel.getResult().isPresent());
    assertFalse(voidFeatureModel.getResult().get());
  }

  @Test
  public void testFilter1() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/FalseOptional.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/CompleteToValid.fc", "CompleteToValid");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Filter filter = new Filter(featureConfiguration.getAstNode());
    tool.addAnalysis(filter);
    tool.performAnalyses();

    assertTrue(filter.getResult().isPresent());
    assertFalse(filter.getResult().get().isEmpty());
    assertEquals(1, filter.getResult().get().size());
  }

  @Test
  public void testFilter2() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/FalseOptional.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/ValidConfig.fc", "ValidConfig");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Filter filter = new Filter(featureConfiguration.getAstNode());
    tool.addAnalysis(filter);
    tool.performAnalyses();

    assertTrue(filter.getResult().isPresent());
    assertFalse(filter.getResult().get().isEmpty());
    assertEquals(2, filter.getResult().get().size());
  }

  @Test
  public void testFilter3() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/FalseOptional.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/InvalidConfig.fc", "InvalidConfig");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Filter filter = new Filter(featureConfiguration.getAstNode());
    tool.addAnalysis(filter);
    tool.performAnalyses();

    assertTrue(filter.getResult().isPresent());
    assertFalse(filter.getResult().get().isEmpty());
  }

  @Test
  public void testisValid1() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/FalseOptional.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/CompleteToValid.fc", "CompleteToValid");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    IsValid isValid = new IsValid(featureConfiguration.getAstNode());
    tool.addAnalysis(isValid);
    tool.performAnalyses();

    assertTrue(isValid.getResult().isPresent());
    assertFalse(isValid.getResult().get());
  }
  @Test
  public void testisValid2() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/FalseOptional.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/ValidConfig.fc", "ValidConfig");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "FalseOptional");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    IsValid isValid = new IsValid(featureConfiguration.getAstNode());
    tool.addAnalysis(isValid);
    tool.performAnalyses();

    assertTrue(isValid.getResult().isPresent());
    assertTrue(isValid.getResult().get());
  }
  @Test
  public void testisValid3() throws IOException{
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable("src/test/resources/DeadFeatures.fd");
    FeatureConfigurationSymbol featureConfiguration = setupConfigSymTab("src/test/resources/InvalidConfig.fc", "InvalidConfig");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "DeadFeatures");
    FeatureModelAnalysisTool tool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    IsValid isValid = new IsValid(featureConfiguration.getAstNode());
    tool.addAnalysis(isValid);
    tool.performAnalyses();

    assertTrue(isValid.getResult().isPresent());
    assertFalse(isValid.getResult().get());
  }

  @Test
  public void testAllProducts() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "../fd-lang/src/test/resources/fdvalid/Phone.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Phone");

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    AllProducts allProducts = new AllProducts();
    modelAnalysisTool.addAnalysis(allProducts);
    modelAnalysisTool.performAnalyses();
    assertTrue(allProducts.getResult().isPresent());
    assertEquals(84, allProducts.getResult().get().size());

  }

  @Test
  public void testFindValid1() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "../fd-lang/src/test/resources/fdvalid/Phone.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Phone");

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    FindValidConfig findValidConfig = new FindValidConfig();
    modelAnalysisTool.addAnalysis(findValidConfig);
    modelAnalysisTool.performAnalyses();
    assertTrue(findValidConfig.getResult().isPresent());
  }

  @Test
  public void testFindValid2() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
            "src/test/resources/Void.fd");
    FeatureDiagramSymbol featureDiagramSymbol = setupFeatureDiagramm(featureDiagramArtifactScope, "Void");

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    FindValidConfig findValidConfig = new FindValidConfig();
    modelAnalysisTool.addAnalysis(findValidConfig);
    modelAnalysisTool.performAnalyses();
    assertFalse(findValidConfig.getResult().isPresent());
  }

}
