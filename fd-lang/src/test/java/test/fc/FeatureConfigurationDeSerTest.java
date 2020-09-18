/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationScopeDeSer;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureConfigurationDeSerTest extends AbstractTest {

  protected static final FeatureConfigurationScopeDeSer deSer = new FeatureConfigurationScopeDeSer();

  protected static final ModelPath mp = new ModelPath(Paths.get("src/test/resources"));

  @BeforeClass
  public static void initDeSer() {
    deSer.setGlobalScope(FeatureConfigurationTool.createGlobalScope(mp));
  }

  protected IFeatureConfigurationArtifactScope setupSymbolTable(String modelFile) {
    return FeatureConfigurationTool.createSymbolTable("src/test/resources/" + modelFile, mp);
  }

  @Test
  public void testRoundtripSerialization() {
    IFeatureConfigurationArtifactScope scope = setupSymbolTable("fcvalid/SelectSome.fc");
    assertTrue(null != scope);
    String serialized = deSer.serialize(scope);
    assertTrue(null != serialized);

    IFeatureConfigurationArtifactScope deserializedScope = deSer.deserialize(serialized);
    assertTrue(deserializedScope instanceof FeatureConfigurationArtifactScope);
    FeatureConfigurationArtifactScope deserialized = (FeatureConfigurationArtifactScope) deserializedScope;

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
    IFeatureConfigurationArtifactScope scope = deSer
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
    deSer.setSymbolFileExtension("fcsym");
    IFeatureConfigurationArtifactScope fcScope = setupSymbolTable("fcvalid/BasicCarNavigation.fc");
    deSer.store(fcScope, Paths.get("target/test-symbols"));

    Path expectedPath = Paths.get("target/test-symbols/BasicCarNavigation.fcsym");
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
