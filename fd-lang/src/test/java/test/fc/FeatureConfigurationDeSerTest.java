/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbols2Json;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureConfigurationDeSerTest extends AbstractLangTest {

  protected static final ModelPath mp = new ModelPath(Paths.get("src/test/resources"));

  protected IFeatureConfigurationArtifactScope setupSymbolTable(String modelFile) {
    return fcTool.createSymbolTable("src/test/resources/" + modelFile, mp, fcParser);
  }

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationMill.init();
  }

  @Before
  public void setUpFCLog() {
    //  Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
  }

  @Test
  public void testRoundtripSerialization() {
    IFeatureConfigurationArtifactScope scope = setupSymbolTable("fcvalid/SelectSome.fc");
    assertTrue(null != scope);
    String serialized = fcDeSer.serialize(scope);
    assertTrue(null != serialized);

    IFeatureConfigurationArtifactScope deserializedScope = fcDeSer.deserialize(serialized);
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
    ModelPaths.addEntry(FeatureDiagramMill.globalScope().getModelPath(),
        "src/test/resources");
    FeatureConfigurationSymbols2Json s2j = new FeatureConfigurationSymbols2Json();

    IFeatureConfigurationArtifactScope scope = s2j
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
    IFeatureConfigurationArtifactScope fcScope = setupSymbolTable("fcvalid/BasicCarNavigation.fc");
    FeatureConfigurationSymbols2Json s2j = new FeatureConfigurationSymbols2Json();
    s2j.store(fcScope, "target/test-symbols/fcvalid/BasicCarNavigation.fcsym");

    Path expectedPath = Paths.get("target/test-symbols/fcvalid/BasicCarNavigation.fcsym");
    assertTrue(expectedPath.toFile().exists());

    String expected = "{\"generated-using\":\"www.MontiCore.de technology\","
        + "\"name\":\"BasicCarNavigation\","
        + "\"package\":\"fcvalid\","
        + "\"symbols\":[{"
        + "\"kind\":\"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\","
        + "\"name\":\"BasicCarNavigation\","
        + "\"featureDiagram\":\"fdvalid.CarNavigation\","
        + "\"selectedFeatures\":["
        + "\"CarNavigation\","
        + "\"Display\","
        + "\"GPS\","
        + "\"Memory\","
        + "\"VoiceControl\","
        + "\"Small\","
        + "\"SmallScreen\"]}]}";
    String actual = FileReaderWriter.readFromFile(expectedPath);
    assertEquals(expected, actual);
  }

}
