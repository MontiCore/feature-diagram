/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.paths.MCPath;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureDiagramSymbolTableTest extends AbstractLangTest {

  @BeforeClass
  public static void initMills() {
    FeatureDiagramMill.init();
    FeatureDiagramMill.globalScope();
  }

  protected IFeatureDiagramArtifactScope setupSymbolTable(String modelFile, MCPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    IFeatureDiagramGlobalScope gs = FeatureDiagramMill.globalScope();
    ModelPaths.merge(gs.getSymbolPath(), mp);
    return FeatureDiagramMill.scopesGenitorDelegator().createFromAST(ast);
  }

  protected IFeatureDiagramArtifactScope setupSymbolTable(String modelFile)
      throws IOException {
    return setupSymbolTable(modelFile, new MCPath(Paths.get("src", "test", "resources")));
  }

  @Test
  public void test() throws IOException {
    String model = "src/test/resources/fdvalid/BasicElements.fd";
    IFeatureDiagramArtifactScope scope = setupSymbolTable(model);

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

  @Test
  public void testImport() throws IOException {
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "src/test/resources/fdvalid/LeafImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.LeafImport.C");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.LeafImport.C", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope.resolveFeature("fdvalid.LeafImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.LeafImport.H", featureSymbolH.getFullName());
  }

  @Test
  public void testTransitiveImport() throws IOException {
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "src/test/resources/fdvalid/TransitiveImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope
        .resolveFeature("fdvalid.TransitiveImport.AA");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.TransitiveImport.AA", featureSymbol.getFullName());

    featureSymbolOpt = fdScope.resolveFeature("fdvalid.TransitiveImport.X");
    assertTrue(featureSymbolOpt.isPresent());
    featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.TransitiveImport.X", featureSymbol.getFullName());

    featureSymbolOpt = fdScope.resolveFeature("fdvalid.TransitiveImport.Y");
    assertTrue(featureSymbolOpt.isPresent());
    featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.TransitiveImport.Y", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope
        .resolveFeature("fdvalid.TransitiveImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.TransitiveImport.H", featureSymbolH.getFullName());
  }

  @Test
  public void testRootImport() throws IOException {
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "src/test/resources/fdvalid/RootImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.RootImport.Y");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.RootImport.Y", featureSymbol.getFullName());

    featureSymbolOpt = fdScope.resolveFeature("fdvalid.RootImport.M");
    assertTrue(featureSymbolOpt.isPresent());
    featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.RootImport.M", featureSymbol.getFullName());

    Optional<FeatureDiagramSymbol> featureDiagramSymbolOpt = fdScope
        .resolveFeatureDiagram("fdvalid.RootImport");
    assertTrue(featureDiagramSymbolOpt.isPresent());
    FeatureDiagramSymbol featureDiagramSymbol = featureDiagramSymbolOpt.get();
    assertEquals("A", featureDiagramSymbol.getAstNode().getRootFeature());
  }

  @Test
  public void testSurroundedImport() throws IOException {
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "src/test/resources/fdvalid/SurroundedImport.fd");
    Optional<FeatureSymbol> featureSymbolOpt = fdScope.resolveFeature("fdvalid.SurroundedImport.C");
    assertTrue(featureSymbolOpt.isPresent());
    FeatureSymbol featureSymbol = featureSymbolOpt.get();
    assertEquals("fdvalid.SurroundedImport.C", featureSymbol.getFullName());

    Optional<FeatureSymbol> featureSymbolOptH = fdScope
        .resolveFeature("fdvalid.SurroundedImport.H");
    assertTrue(featureSymbolOptH.isPresent());
    FeatureSymbol featureSymbolH = featureSymbolOptH.get();
    assertEquals("fdvalid.SurroundedImport.H", featureSymbolH.getFullName());
  }

}