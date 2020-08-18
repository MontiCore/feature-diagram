/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.solver;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;

import java.util.List;

public interface ISolver {
  public List<ASTFeatureConfiguration> solve(String model, List<String> featureNames,
      Boolean isAllsSolutions);

  public void setFeatureDiagrammName(String name);
}
