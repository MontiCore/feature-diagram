/* (c) https://github.com/MontiCore/monticore */
package featurediagram;

import de.monticore.io.paths.ModelPath;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Assert;
import org.junit.Test;
import tool.FeatureModelAnalysisTool;
import tool.analyses.Analysis;
import tool.analyses.NumberOfProducts;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;

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

  @Test
  public void testPhoneExample() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
        "../fd-lang/src/test/resources/fdvalid/Phone.fd");
    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol = featureDiagramArtifactScope
        .resolveFeatureDiagram("Phone");
    Assert.assertTrue(optionalFeatureDiagramSymbol.isPresent());
    FeatureDiagramSymbol featureDiagramSymbol = optionalFeatureDiagramSymbol.get();

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    System.out.println(numberOfProducts.getResult());

  }

  @Test
  public void testPhoneComplexExample() throws IOException {
    FeatureDiagramArtifactScope featureDiagramArtifactScope = setupSymbolTable(
        "../fd-lang/src/test/resources/fdvalid/PhoneComplex.fd");
    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol = featureDiagramArtifactScope
        .resolveFeatureDiagram("Phone");
    Assert.assertTrue(optionalFeatureDiagramSymbol.isPresent());
    FeatureDiagramSymbol featureDiagramSymbol = optionalFeatureDiagramSymbol.get();
    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    System.out.println(numberOfProducts.getResult());

  }
}
