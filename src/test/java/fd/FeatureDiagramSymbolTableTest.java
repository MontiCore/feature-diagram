package fd;
/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import de.monticore.io.paths.ModelPath;
import featurediagram.FeatureDiagramTool;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class FeatureDiagramSymbolTableTest {

  @Test
  public void test() {
    String model = "src/test/resources/fdvalid/BasicElements.fd";
    FeatureDiagramArtifactScope scope = FeatureDiagramTool.run(model, new ModelPath());
    assertTrue(null!= scope);
    FeatureDiagramSymbol fd = scope.resolveFeatureDiagram("BasicElements").orElse(null);
    assertTrue(null != fd);

    assertTrue(scope.resolveFeatureDown("BasicElements.A").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.B").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.C").isPresent());
    assertTrue(scope.resolveFeatureDown("BasicElements.D").isPresent());
    assertFalse(scope.resolveFeatureDown("BasicElements.NotAFeature").isPresent());
    assertFalse(scope.resolveFeatureDown("A").isPresent());

    IFeatureDiagramScope fdScope = fd.getSpannedScope();
    assertTrue(fdScope.resolveFeature("A").isPresent());
    assertTrue(fdScope.resolveFeature("A").isPresent());
    assertTrue(fdScope.resolveFeature("B").isPresent());
    assertTrue(fdScope.resolveFeature("C").isPresent());
    assertTrue(fdScope.resolveFeature("D").isPresent());
    assertFalse(fdScope.resolveFeature("NotAFeature").isPresent());
  }
  
}
