/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FeatureDiagramSymbolTableTest {

  @BeforeClass
  public static void disableFailQuick() {
//        Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
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

  @Test
  public void test() throws IOException {
    String model = "src/test/resources/fdvalid/BasicElements.fd";
    FeatureDiagramArtifactScope scope = setupSymbolTable(model);

    assertTrue(null != scope);
    FeatureDiagramSymbol fd = scope.resolveFeatureDiagram("BasicElements").orElse(null);
    assertTrue(null != fd);

    assertTrue(scope.resolveFeatureDown("BasicElements.A").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.B").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.C").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.D").isPresent());
    assertFalse(scope.resolveFeatureDown("BasicElements.NotAFeature").isPresent());
    assertFalse(scope.resolveFeatureDown("A").isPresent());

    IFeatureDiagramScope fdScope = fd.getSpannedScope();
    assertTrue(fdScope.resolveFeature("A").isPresent());
    assertTrue(fdScope.resolveFeature("A").isPresent());
    assertTrue(fdScope.resolveFeature("B").isPresent());
    assertTrue(fdScope.resolveFeature("C").isPresent());
    assertTrue(fdScope.resolveFeature("D").isPresent());
    assertFalse(fdScope.resolveFeature("NotAFeature").isPresent());
  }

}
