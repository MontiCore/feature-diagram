/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialArtifactScope;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbols2Json;
import de.monticore.featureconfigurationpartial._symboltable.IFeatureConfigurationPartialArtifactScope;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.Before;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureConfigurationPartialDeSerTest extends AbstractLangTest {

  protected static final MCPath mp = new MCPath(Paths.get("src/test/resources"));

  @Before
  public void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  protected IFeatureConfigurationPartialArtifactScope setupSymbolTable(String modelFile) {
    ASTFCCompilationUnit ast = fcpTool.parse("src/test/resources/" + modelFile);
    return fcpTool.createSymbolTable(ast, mp);
  }

  @Test
  public void testRoundtripSerialization() {
    IFeatureConfigurationPartialArtifactScope scope = setupSymbolTable("pfcvalid/SelectSome.fc");
    assertTrue(null != scope);
    String serialized = fcpSymbols2Json.serialize(scope);
    assertTrue(null != serialized);

    IFeatureConfigurationPartialArtifactScope deserializedScope = fcpSymbols2Json.deserialize(serialized);
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
    ModelPaths.addEntry(FeatureDiagramMill.globalScope().getSymbolPath(),
        "src/test/resources");
    FeatureConfigurationPartialSymbols2Json s2j = new FeatureConfigurationPartialSymbols2Json();
    IFeatureConfigurationPartialArtifactScope scope = s2j
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
    IFeatureConfigurationPartialArtifactScope fcScope = setupSymbolTable(
        "pfcvalid/BasicCarNavigation.fc");
    FeatureConfigurationPartialSymbols2Json s2j = new FeatureConfigurationPartialSymbols2Json();
    s2j.store(fcScope, "target/test-symbols/pfcvalid/BasicCarNavigation.fcsym");

    Path expectedPath = Paths.get("target/test-symbols/pfcvalid/BasicCarNavigation.fcsym");
    assertTrue(expectedPath.toFile().exists());

    String expected = "{"
        + "  \"generated-using\": \"www.MontiCore.de technology\","
        + "  \"name\": \"BasicCarNavigation\","
        + "  \"package\": \"fcvalid\","
        + "  \"symbols\": ["
        + "    {"
        + "      \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\","
        + "      \"name\": \"BasicCarNavigation\","
        + "      \"fullName\": \"fcvalid.BasicCarNavigation\","
        + "      \"packageName\": \"fcvalid\","
        + "      \"featureDiagram\": \"fdvalid.CarNavigation\","
        + "        \"selectedFeatures\": ["
        + "        \"CarNavigation\","
        + "        \"Display\","
        + "        \"GPS\","
        + "        \"Memory\","
        + "        \"VoiceControl\","
        + "        \"Small\","
        + "        \"SmallScreen\""
        + "      ]"
        + "    }"
        + "  ]"
        + "}";
    String actual = FileReaderWriter.readFromFile(expectedPath);

    // ignore whitespace
    expected = expected.replaceAll("\\s", "");
    actual = actual.replaceAll("\\s", "");
    
    assertEquals(expected, actual);
  }

}
