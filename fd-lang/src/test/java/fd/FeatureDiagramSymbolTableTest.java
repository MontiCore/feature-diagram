/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.*;
import featurediagram._symboltable.serialization.FeatureDiagramScopeDeSer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
    return setupSymbolTable(modelFile, new ModelPath(Paths.get("src", "test", "resources")));
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

  @Ignore //Wartet auf SymbolSurrogates, um FeatureSymbol Definition und Nutzung zu handlen
  @Test
  public void testImport() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable("src/test/resources/fdvalid/LeafImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.LeafImport.C");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.LeafImport.C", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope.resolveFeature("fdvalid.LeafImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.LeafImport.H", featureSymbolH.getFullName());
  }

  @Ignore //Wartet auf SymbolSurrogates, um FeatureSymbol Definition und Nutzung zu handlen
  @Test
  public void testTransitiveImport() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable("src/test/resources/fdvalid/TransitiveImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.TransitiveImport.AA");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.TransitiveImport.AA", featureSymbol.getFullName());

    featureSymbolOpt = fdScope.resolveFeature("fdvalid.TransitiveImport.X");
    assertTrue(featureSymbolOpt.isPresent());
    featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.TransitiveImport.X", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope.resolveFeature("fdvalid.TransitiveImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.TransitiveImport.H", featureSymbolH.getFullName());
  }

  @Ignore //Wartet auf SymbolSurrogates, um FeatureSymbol Definition und Nutzung zu handlen
  @Test
  public void testRootImport() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable("src/test/resources/fdvalid/RootImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.RootImport.Y");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.RootImport.Y", featureSymbol.getFullName());

    featureSymbolOpt = fdScope.resolveFeature("fdvalid.RootImport.M");
    assertTrue(featureSymbolOpt.isPresent());
    featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.RootImport.M", featureSymbol.getFullName());

    Optional<FeatureDiagramSymbol> featureDiagramSymbolOpt = fdScope.resolveFeatureDiagram("fdvalid.RootImport");
    assertTrue(featureDiagramSymbolOpt.isPresent());
    FeatureDiagramSymbol featureDiagramSymbol = featureDiagramSymbolOpt.get();
    assertEquals("A", featureDiagramSymbol.getAstNode().getRootFeature());
  }

  @Ignore //Wartet auf SymbolSurrogates, um FeatureSymbol Definition und Nutzung zu handlen
  @Test
  public void testSurroundedImport() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable("src/test/resources/fdvalid/SurroundedImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.SurroundedImport.C");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.SurroundedImport.C", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope.resolveFeature("fdvalid.SurroundedImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.SurroundedImport.H", featureSymbolH.getFullName());
  }

  @Test
  public void testDeSer() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable("src/test/resources/fdvalid/CarNavigation.fd");
    new FeatureDiagramScopeDeSer().store(fdScope, Paths.get("target/test-symbols"));
    assertTrue(new File("target/symbols/CarNavigation.fdsym").exists());
  }
}