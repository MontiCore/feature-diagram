/* (c) https://github.com/MontiCore/monticore */
package featurediagram;

import complexconstraintfeaturediagram._ast.ASTConstraint;
import complexconstraintfeaturediagram._parser.ComplexConstraintFeatureDiagramParser;
import complexconstraintfeaturediagram._symboltable.ComplexConstraintFeatureDiagramArtifactScope;
import complexconstraintfeaturediagram._symboltable.ComplexConstraintFeatureDiagramGlobalScope;
import complexconstraintfeaturediagram._symboltable.ComplexConstraintFeatureDiagramLanguage;
import complexconstraintfeaturediagram._symboltable.ComplexConstraintFeatureDiagramSymbolTableCreatorDelegator;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.io.paths.ModelPath;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Assert;
import org.junit.Test;
import tool.FeatureModelAnalysisTool;
import tool.analyses.Analysis;
import tool.analyses.NumberOfProducts;
import tool.transform.trafos.BasicConstraintTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  protected ComplexConstraintFeatureDiagramArtifactScope setupComplexSymbolTable(String modelFile,
      ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new ComplexConstraintFeatureDiagramParser().parse(modelFile)
        .orElse(null);
    assertNotNull(ast);
    ComplexConstraintFeatureDiagramLanguage lang = new ComplexConstraintFeatureDiagramLanguage();
    ComplexConstraintFeatureDiagramGlobalScope globalScope = new ComplexConstraintFeatureDiagramGlobalScope(
        mp, lang);
    ComplexConstraintFeatureDiagramSymbolTableCreatorDelegator symbolTable = lang
        .getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

  protected ComplexConstraintFeatureDiagramArtifactScope setupComplexSymbolTable(String modelFile)
      throws IOException {
    return setupComplexSymbolTable(modelFile, new ModelPath());
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
    modelAnalysisTool.addFeatureModelTrafo(new BasicConstraintTrafo());
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    System.out.println(numberOfProducts.getResult());

  }

  @Test
  public void testPhoneComplexExample() throws IOException {
    ComplexConstraintFeatureDiagramArtifactScope featureDiagramArtifactScope = setupComplexSymbolTable(
        "../fd-lang/src/test/resources/fdvalid/PhoneComplex.fd");
    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol = featureDiagramArtifactScope
        .resolveFeatureDiagram("Phone");
    Assert.assertTrue(optionalFeatureDiagramSymbol.isPresent());
    FeatureDiagramSymbol featureDiagramSymbol = optionalFeatureDiagramSymbol.get();

    List<ASTExpression> constraints = featureDiagramSymbol.getAstNode().streamFDElements()
        .filter(x -> x instanceof ASTConstraint).map(x -> ((ASTConstraint) x).getExpression())
        .collect(Collectors.toList());

    FeatureModelAnalysisTool modelAnalysisTool = new FeatureModelAnalysisTool(featureDiagramSymbol);
    modelAnalysisTool.addFeatureModelTrafo(new ComplexConstraint2FZN(constraints));
    Analysis<Integer> numberOfProducts = new NumberOfProducts();
    modelAnalysisTool.addAnalysis(numberOfProducts);
    modelAnalysisTool.performAnalyses();
    System.out.println(numberOfProducts.getResult());

  }
}
