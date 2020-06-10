/* (c) https://github.com/MontiCore/monticore */
package tool.solver;

import featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.List;
import java.util.Map;

public interface ISolver {
  public List<ASTFeatureConfiguration> solve(String model, List<String> featureNames,
                                             Boolean isAllsSolutions);
  public void setFeatureDiagrammName(String name);
}
