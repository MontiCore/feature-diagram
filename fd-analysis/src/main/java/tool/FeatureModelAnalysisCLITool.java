/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import tool.solver.ISolver;

public class FeatureModelAnalysisCLITool extends FeatureModelAnalysisTool {

  public FeatureModelAnalysisCLITool(
      FeatureDiagramSymbol featureModelSymbol, ISolver solver) {
    super(featureModelSymbol, solver);
  }

  public FeatureModelAnalysisCLITool(
      FeatureDiagramSymbol featureSymbol) {
    super(featureSymbol);
  }
}
