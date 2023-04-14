/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class AbstractTest {

  protected FeatureDiagramTool fdTool = new FeatureDiagramTool();

  protected FeatureDiagramParser fdParser = new FeatureDiagramParser();

  protected FeatureConfigurationTool fcTool = new FeatureConfigurationTool();

  protected FeatureConfigurationParser fcParser = new FeatureConfigurationParser();

  public static final String TEST_RES = "src/test/resources/";


  @Before
  public void setup() {
    LogStub.init();
    Log.initWARN();
    Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    Log.getFindings().clear();
    FeatureConfigurationPartialMill.reset();
    FeatureConfigurationPartialMill.init();
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

  protected ASTFeatureDiagram getFD(String modelFile) {
    ASTFDCompilationUnit ast = fdTool.parse(TEST_RES + modelFile);
    IFeatureDiagramArtifactScope as = fdTool
        .createSymbolTable(ast);
    String modelName = modelFile.replace(".fd", "");
    if (modelName.contains("/")) {
      modelName = modelName.substring(modelFile.lastIndexOf("/") + 1);
    }

    Optional<FeatureDiagramSymbol> optionalFeatureDiagramSymbol = as
        .resolveFeatureDiagram(modelName);
    assertTrue(optionalFeatureDiagramSymbol.isPresent());
    assertNotNull(optionalFeatureDiagramSymbol.get());
    assertNotNull(optionalFeatureDiagramSymbol.get().getAstNode());
    return optionalFeatureDiagramSymbol.get().getAstNode();
  }

  protected ASTFeatureConfiguration getFC(String modelFile) {
    ASTFCCompilationUnit config = fcTool.parse(TEST_RES + modelFile);
    IFeatureConfigurationArtifactScope symbolTable = fcTool
        .createSymbolTable(config, new MCPath(Paths.get(TEST_RES)));

    String modelName = modelFile.replace(".fc", "");
    if (modelName.contains("/")) {
      modelName = modelName.substring(modelFile.lastIndexOf("/") + 1);
    }

    Optional<FeatureConfigurationSymbol> featureConfOpt = symbolTable
        .resolveFeatureConfiguration(modelName);
    assertTrue(featureConfOpt.isPresent());
    assertNotNull(featureConfOpt.get());
    return featureConfOpt.get().getAstNode();
  }

}

