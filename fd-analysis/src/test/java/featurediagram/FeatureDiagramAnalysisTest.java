/* (c) https://github.com/MontiCore/monticore */
package featurediagram;

import de.monticore.io.paths.ModelPath;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Assert;
import org.junit.Test;
import tool.FeatureModelAnalysisTool;
import tool.analyses.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureDiagramAnalysisTest {

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
    System.out.println(numberOfProducts.getResult());
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
}
