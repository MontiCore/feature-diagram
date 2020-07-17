/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import tool.analyses.Analysis;
import tool.solver.ISolver;
import tool.solver.ChocoSolver;
import tool.transform.FZNModelBuilder;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.trafos.BasicTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;
import tool.transform.trafos.RootFeatureSelected;

import java.util.ArrayList;
import java.util.List;

public class FeatureModelAnalysisTool {

  private ASTFeatureDiagram featureModel;

  private List<Analysis> analyses = new ArrayList<>();

  private ISolver solver;

  private List<FeatureModel2FlatZincModelTrafo> trafos = new ArrayList<>();

  public FeatureModelAnalysisTool(ASTFeatureDiagram featureModel, ISolver solver) {
    this.featureModel = featureModel;
    this.solver = solver;
    trafos.add(new BasicTrafo());
    trafos.add(new RootFeatureSelected());
    trafos.add(new ComplexConstraint2FZN());
  }

  public FeatureModelAnalysisTool(ASTFeatureDiagram featureModel) {
    this(featureModel, new ChocoSolver());
}

  public void addFeatureModelTrafo(FeatureModel2FlatZincModelTrafo trafo) {
    trafos.add(trafo);
  }

  public ASTFeatureDiagram getFeatureModel() {
    return featureModel;
  }

  public void setFeatureModel(ASTFeatureDiagram featureModel) {
    this.featureModel = featureModel;
  }

  public void addAnalysis(Analysis analysis) {
    this.analyses.add(analysis);
  }

  public void performAnalyses() {
    solver.setFeatureDiagrammName(featureModel.getName());
    analyses.forEach(
        analysis -> {
          FZNModelBuilder modelPrinter = analysis.getModelBuilder();
          modelPrinter.addAllFeatureModelFZNTrafos(trafos);
          modelPrinter.buildFlatZincModel(featureModel);
          analysis.setFeatureModel(featureModel);
          String s = modelPrinter.getFlatZincModel().print();
          analysis.perform(solver.solve(s, featureModel.getAllFeatures(), modelPrinter.isAllSolutions()));
        }
    );
  }

}
