/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationArtifactScope;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;
import test.AbstractTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FeatureConfigurationSymbolTableTest extends AbstractTest {

  protected FeatureConfigurationArtifactScope setupSymbolTable(String modelFile) {
    ModelPath mp = new ModelPath(Paths.get("src", "test", "resources"));
    return FeatureConfigurationTool.createSymbolTable(modelFile, mp);
  }

  @Test
  public void testSymbolTableCreation() {
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/BasicCarNavigation.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/PremiumCarNavigation.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectImported.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectNone.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectOne.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectSome.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/StarImport.fc"));
  }

  @Test
  public void testDetail() {
    String model = "src/test/resources/fcvalid/BasicCarNavigation.fc";
    FeatureConfigurationArtifactScope scope = setupSymbolTable(model);

    assertTrue(null != scope);
    FeatureConfigurationSymbol fd = scope.resolveFeatureConfiguration("BasicCarNavigation")
        .orElse(null);
    assertTrue(null != fd);
    assertEquals("BasicCarNavigation", fd.getName());
    assertEquals("CarNavigation", fd.getFeatureDiagram().getName());

    assertEquals(7, fd.sizeSelectedFeatures());
    List<String> selectedFeatureNames = fd.getSelectedFeatureList().stream().map(f -> f.getName())
        .collect(Collectors.toList());

    assertTrue(selectedFeatureNames.contains("CarNavigation"));
    assertTrue(selectedFeatureNames.contains("VoiceControl"));
    assertTrue(selectedFeatureNames.contains("Display"));
    assertTrue(selectedFeatureNames.contains("SmallScreen"));
    assertTrue(selectedFeatureNames.contains("GPS"));
    assertTrue(selectedFeatureNames.contains("Memory"));
    assertTrue(selectedFeatureNames.contains("Small"));
  }

  @Test
  public void testImport() {
    String model = "src/test/resources/fcvalid/SelectImported.fc";
    FeatureConfigurationArtifactScope scope = setupSymbolTable(model);

    assertTrue(null != scope);
    FeatureConfigurationSymbol fd = scope.resolveFeatureConfiguration("SelectImported")
        .orElse(null);
    assertTrue(null != fd);
    assertEquals(2, fd.sizeSelectedFeatures());
    List<String> selectedFeatureNames = fd.getSelectedFeatureList().stream().map(f -> f.getName())
        .collect(Collectors.toList());

    assertTrue(selectedFeatureNames.contains("AA"));
    assertTrue(selectedFeatureNames.contains("B"));
  }

}