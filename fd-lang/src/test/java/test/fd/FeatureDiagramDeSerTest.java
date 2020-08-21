/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;
import test.AbstractTest;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureDiagramDeSerTest extends AbstractTest {

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile) {
    return FeatureDiagramTool.createSymbolTable("src/test/resources/" + modelFile, new ModelPath());
  }

  @Test
  public void testSerializeDeserialize() {
    FeatureDiagramScopeDeSer deSer = new FeatureDiagramScopeDeSer();
    FeatureDiagramArtifactScope scope = setupSymbolTable("fdvalid/BasicElements.fd");
    assertTrue(null != scope);
    String serialized = deSer.serialize(scope);
    assertTrue(null != serialized);

    IFeatureDiagramScope deserializedScope = deSer.deserialize(serialized);
    assertTrue(deserializedScope instanceof FeatureDiagramArtifactScope);
    FeatureDiagramArtifactScope deserialized = (FeatureDiagramArtifactScope) deserializedScope;

    assertEquals(scope.getName(), deserialized.getName());
    assertEquals(scope.getPackageName(), deserialized.getPackageName());
    assertEquals(scope.getImportsList().size(),
      deserialized.getImportsList().size());
    assertEquals(scope.getTopLevelSymbol().isPresent(),
        deserialized.getTopLevelSymbol().isPresent());
    assertEquals(scope.getTopLevelSymbol().get().getName(),
        deserialized.getTopLevelSymbol().get().getName());
    assertEquals(scope.getLocalFeatureDiagramSymbols().size(),
        deserialized.getLocalFeatureDiagramSymbols().size());
    assertEquals(scope.getLocalFeatureSymbols().size(),
        deserialized.getLocalFeatureSymbols().size());

    FeatureDiagramSymbol expectedSymbol = scope.getLocalFeatureDiagramSymbols().get(0);
    FeatureDiagramSymbol actualSymbol = deserialized.getLocalFeatureDiagramSymbols().get(0);
    assertEquals(expectedSymbol.getName(), actualSymbol.getName());
    assertEquals(expectedSymbol.getSpannedScope().getName(),
        actualSymbol.getSpannedScope().getName());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureSymbols().size(),
        actualSymbol.getSpannedScope().getLocalFeatureSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSubScopes().size(),
        actualSymbol.getSpannedScope().getSubScopes().size());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size(),
        actualSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSpanningSymbol().getName(),
        actualSymbol.getSpannedScope().getSpanningSymbol().getName());
  }

  @Test
  public void testDeSer() {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "fdvalid/CarNavigation.fd");
    new FeatureDiagramScopeDeSer().store(fdScope, Paths.get("target/test-symbols"));
    assertTrue(new File("target/test-symbols/CarNavigation.fdsym").exists());
  }

}
