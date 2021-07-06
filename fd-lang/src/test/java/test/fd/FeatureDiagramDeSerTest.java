/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.FileReaderWriter;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureDiagramDeSerTest extends AbstractLangTest {

  protected IFeatureDiagramArtifactScope setupSymbolTable(String modelFile) {
    ASTFDCompilationUnit ast = fdTool.parse("src/test/resources/" + modelFile, fdParser);
    return fdTool.createSymbolTable(ast);
  }

  @BeforeClass
  public static void initMills() {
    FeatureDiagramMill.init();
  }

  @Test
  public void testRoundtripSerialization() {
    IFeatureDiagramArtifactScope scope = setupSymbolTable("fdvalid/BasicElements.fd");
    assertTrue(null != scope);
    String serialized = fdSymbols2Json.serialize(scope);
    assertTrue(null != serialized);

    IFeatureDiagramScope deserializedScope = fdSymbols2Json.deserialize(serialized);
    assertTrue(deserializedScope instanceof FeatureDiagramArtifactScope);
    FeatureDiagramArtifactScope deserialized = (FeatureDiagramArtifactScope) deserializedScope;

    assertEquals(scope.getName(), deserialized.getName());
    assertEquals(scope.getPackageName(), deserialized.getPackageName());
    assertEquals(scope.getImportsList().size(), deserialized.getImportsList().size());
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
  public void testLoad() {
    FeatureDiagramSymbols2Json s2j = new FeatureDiagramSymbols2Json();
    IFeatureDiagramArtifactScope scope = s2j
        .load("src/test/resources/symbols/CarNavigation.fdsym");
    assertTrue(null != scope);
    assertEquals("CarNavigation", scope.getName());
    assertEquals("fdvalid", scope.getPackageName());
    assertEquals(0, scope.getImportsList().size());
    assertEquals(true, scope.getTopLevelSymbol().isPresent());
    assertEquals("CarNavigation", scope.getTopLevelSymbol().get().getName());
    assertEquals(1, scope.getLocalFeatureDiagramSymbols().size());
    assertEquals(0, scope.getLocalFeatureSymbols().size());

    FeatureDiagramSymbol actualSymbol = scope.getLocalFeatureDiagramSymbols().get(0);
    assertEquals("CarNavigation", actualSymbol.getName());
    assertEquals("CarNavigation", actualSymbol.getSpannedScope().getName());
    assertEquals(17, actualSymbol.getSpannedScope().getLocalFeatureSymbols().size());
    assertEquals(0, actualSymbol.getSpannedScope().getSubScopes().size());
    assertEquals(0, actualSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size());
    assertEquals("CarNavigation", actualSymbol.getSpannedScope().getSpanningSymbol().getName());
  }

  @Test
  public void testStore() {
    JsonPrinter.enableIndentation();
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable("fdvalid/CarNavigation.fd");
    FeatureDiagramSymbols2Json s2j = new FeatureDiagramSymbols2Json();
    s2j.store(fdScope, "target/test-symbols/fdvalid/CarNavigation.fdsym");

    Path expectedPath = Paths.get("target/test-symbols/fdvalid/CarNavigation.fdsym");
    assertTrue(expectedPath.toFile().exists());

    String expected = "{"
        + "  \"generated-using\": \"www.MontiCore.de technology\","
        + "  \"name\": \"CarNavigation\","
        + "  \"package\": \"fdvalid\","
        + "  \"symbols\": ["
        + "    {"
        + "      \"kind\": \"de.monticore.featurediagram._symboltable.FeatureDiagramSymbol\","
        + "      \"name\": \"CarNavigation\","
        + "        \"features\": ["
        + "        \"CarNavigation\","
        + "        \"Display\","
        + "        \"GPS\","
        + "        \"PreinstalledMaps\","
        + "        \"Memory\","
        + "        \"VoiceControl\","
        + "        \"TouchControl\","
        + "        \"Small\","
        + "        \"Medium\","
        + "        \"Large\","
        + "        \"SmallScreen\","
        + "        \"LargeScreen\","
        + "        \"Europe\","
        + "        \"NorthAmerica\","
        + "        \"SouthAmerica\","
        + "        \"Asia\","
        + "        \"Africa\""
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
