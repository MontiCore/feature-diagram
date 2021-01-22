/* (c) https://github.com/MontiCore/monticore */

package test;

import de.monticore.featureconfiguration.FeatureConfigurationCLI;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationDeSer;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialCLI;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.monticore.featureconfigurationpartial._symboltable.FeatureConfigurationPartialDeSer;
import de.monticore.featurediagram.FeatureDiagramCLI;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramDeSer;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.Optional;

import static org.junit.Assert.fail;

public class AbstractLangTest {

  protected FeatureDiagramCLI fdTool = new FeatureDiagramCLI();

  protected FeatureDiagramParser fdParser = new FeatureDiagramParser();

  protected FeatureDiagramDeSer fdDeSer = new FeatureDiagramDeSer();

  protected FeatureConfigurationCLI fcTool = new FeatureConfigurationCLI();

  protected FeatureConfigurationParser fcParser = new FeatureConfigurationParser();

  protected FeatureConfigurationDeSer fcDeSer = new FeatureConfigurationDeSer();

  protected FeatureConfigurationPartialDeSer fcpDeSer = new FeatureConfigurationPartialDeSer();

  protected FeatureConfigurationPartialParser fcpParser = new FeatureConfigurationPartialParser();

  protected FeatureConfigurationPartialCLI fcpTool = new FeatureConfigurationPartialCLI();

  @BeforeClass
  public static void setUpLog() {
//  Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
    FeatureDiagramMill.reset();
    FeatureConfigurationMill.reset();
    FeatureConfigurationPartialMill.reset();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
  }

  @Before
  public void clearGlobalScopes() {
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
