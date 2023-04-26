/* (c) https://github.com/MontiCore/monticore */

package test;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbols2Json;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialTool;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialSymbols2Json;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbols2Json;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;

import java.util.Optional;

import static org.junit.Assert.fail;

public class AbstractLangTest {

  protected FeatureDiagramTool fdTool = new FeatureDiagramTool();

  protected FeatureDiagramParser fdParser = new FeatureDiagramParser();

  protected FeatureDiagramSymbols2Json fdSymbols2Json = new FeatureDiagramSymbols2Json();

  protected FeatureConfigurationTool fcTool = new FeatureConfigurationTool();

  protected FeatureConfigurationParser fcParser = new FeatureConfigurationParser();

  protected FeatureConfigurationSymbols2Json fcSymbols2Json = new FeatureConfigurationSymbols2Json();

  protected FeatureConfigurationPartialSymbols2Json fcpSymbols2Json = new FeatureConfigurationPartialSymbols2Json();

  protected FeatureConfigurationPartialParser fcpParser = new FeatureConfigurationPartialParser();

  protected FeatureConfigurationPartialTool fcpTool = new FeatureConfigurationPartialTool();

  @Before
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    FeatureConfigurationPartialMill.reset();
    FeatureConfigurationPartialMill.globalScope().clear();
    FeatureConfigurationMill.globalScope().clear();
    FeatureDiagramMill.globalScope().clear();
  }


  protected static void assertPresent(Optional<?> opt) {
    Assert.assertTrue(opt.isPresent());
  }

  protected static void assertEmpty(Optional<?> opt) {
    Assert.assertTrue(!opt.isPresent());
  }

  public static void assertErrorCode(String... errorCodes) {
    for (String errorCode : errorCodes) {
      assertErrorCode(errorCode);
    }
    for (Finding finding : Log.getFindings()) {
      if (finding.isError()) {
        fail("Found error '" + finding.getMsg() + "' that was not expected!");
      }
    }
  }

  public static void assertErrorCode(String errorCode) {
    for (Finding finding : Log.getFindings()) {
      if (finding.getMsg().startsWith(errorCode)) {
        //remove finding to enable finding the same error code multiple times
        Log.getFindings().remove(finding);
        return;
      }
    }
    fail("Expected to find an error with the code '" + errorCode + "', but it did not occur!");
  }

}
