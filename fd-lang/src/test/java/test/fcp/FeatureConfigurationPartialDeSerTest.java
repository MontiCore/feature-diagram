/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.Before;
import org.junit.Test;
import test.AbstractTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureConfigurationPartialDeSerTest extends AbstractTest {

  protected static final ModelPath mp = new ModelPath(Paths.get("src/test/resources"));

  @Before
  public void initDeSer() {
    fcpDeSer.setGlobalScope(fcpTool.createGlobalScope(mp));
  }

  protected IFeatureConfigurationPartialArtifactScope setupSymbolTable(String modelFile) {
    return fcpTool.createSymbolTable("src/test/resources/" + modelFile, mp, fcpParser);
  }

  @Test
  public void testRoundtripSerialization() {
    IFeatureConfigurationPartialArtifactScope scope = setupSymbolTable("pfcvalid/SelectSome.fc");
    assertTrue(null != scope);
    String serialized = fcpDeSer.serialize(scope);
    assertTrue(null != serialized);

    IFeatureConfigurationPartialArtifactScope deserializedScope = fcpDeSer.deserialize(serialized);
    assertTrue(deserializedScope instanceof FeatureConfigurationPartialArtifactScope);
    FeatureConfigurationPartialArtifactScope deserialized = (FeatureConfigurationPartialArtifactScope) deserializedScope;

    assertEquals(scope.getName(), deserialized.getName());
    assertEquals(scope.getPackageName(), deserialized.getPackageName());
    assertEquals(scope.getImportsList().size(), deserialized.getImportsList().size());
    assertEquals(scope.getTopLevelSymbol().isPresent(),
        deserialized.getTopLevelSymbol().isPresent());
    assertEquals(scope.getTopLevelSymbol().get().getName(),
        deserialized.getTopLevelSymbol().get().getName());
    assertEquals(scope.getLocalFeatureConfigurationSymbols().size(),
        deserialized.getLocalFeatureConfigurationSymbols().size());
    assertEquals(scope.getLocalFeatureDiagramSymbols().size(),
        deserialized.getLocalFeatureDiagramSymbols().size());
    assertEquals(scope.getLocalFeatureSymbols().size(),
        deserialized.getLocalFeatureSymbols().size());

    FeatureConfigurationSymbol expectedSymbol = scope.getLocalFeatureConfigurationSymbols().get(0);
    FeatureConfigurationSymbol actualSymbol = deserialized.getLocalFeatureConfigurationSymbols()
        .get(0);
    assertEquals(expectedSymbol.getName(), actualSymbol.getName());
    assertEquals(expectedSymbol.getFeatureDiagram().getName(),
        actualSymbol.getFeatureDiagram().getName());
    assertEquals(expectedSymbol.getSelectedFeaturesList().size(),
        actualSymbol.getSelectedFeaturesList().size());
  }

  @Test
  public void testLoad() {
    IFeatureConfigurationPartialArtifactScope scope = fcpDeSer
        .load("src/test/resources/symbols/BasicCarNavigation.fcsym");
    assertTrue(null != scope);
    assertEquals("BasicCarNavigation", scope.getName());
    assertEquals("fcvalid", scope.getPackageName());
    assertEquals(0, scope.getImportsList().size());
    assertEquals(true, scope.getTopLevelSymbol().isPresent());
    assertEquals("BasicCarNavigation", scope.getTopLevelSymbol().get().getName());
    assertEquals(1, scope.getLocalFeatureConfigurationSymbols().size());
    assertEquals(0, scope.getLocalFeatureDiagramSymbols().size());
    assertEquals(0, scope.getLocalFeatureSymbols().size());

    FeatureConfigurationSymbol actualSymbol = scope.getLocalFeatureConfigurationSymbols().get(0);
    assertEquals("BasicCarNavigation", actualSymbol.getName());
    assertEquals("CarNavigation", actualSymbol.getFeatureDiagram().getName());
  }

  @Test
  public void testStore() {
    JsonPrinter.enableIndentation();
    fcpDeSer.setSymbolFileExtension("pfcsym");
    IFeatureConfigurationPartialArtifactScope fcScope = setupSymbolTable(
        "pfcvalid/BasicCarNavigation.fc");
    fcpDeSer.store(fcScope, Paths.get("target/test-symbols"));

    Path expectedPath = Paths.get("target/test-symbols/BasicCarNavigation.pfcsym");
    assertTrue(expectedPath.toFile().exists());

    String expected = "{\n"
        + "  \"name\": \"BasicCarNavigation\",\n"
        + "      \"package\": \"fcvalid\",\n"
        + "      \"symbols\": [\n"
        + "      {\n"
        + "        \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "        \"name\": \"BasicCarNavigation\",\n"
        + "          \"featureDiagram\": \"fdvalid.CarNavigation\",\n"
        + "          \"selectedFeatures\": [\n"
        + "          \"CarNavigation\",\n"
        + "          \"Display\",\n"
        + "          \"GPS\",\n"
        + "          \"Memory\",\n"
        + "          \"VoiceControl\",\n"
        + "          \"Small\",\n"
        + "          \"SmallScreen\"\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }";
    String actual = FileReaderWriter.readFromFile(expectedPath);
    assertEquals(expected, actual);
  }

}
