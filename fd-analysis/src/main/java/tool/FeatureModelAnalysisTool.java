/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import tool.analyses.Analysis;
import tool.solver.ISolver;
import tool.solver.choco.ChocoSolver;
import tool.transform.FZNModelBuilder;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.trafos.BasicTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;
import tool.transform.trafos.RootFeatureSelected;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureModelAnalysisTool {

  private FeatureDiagramSymbol featureSymbol;

  private List<ASTExpression> expressions = new ArrayList<>();

  private List<Analysis> analyses = new ArrayList<>();

  private ISolver solver;

  private List<FeatureModel2FlatZincModelTrafo> trafos = new ArrayList<>();

  public FeatureModelAnalysisTool(FeatureDiagramSymbol featureModelSymbol, ISolver solver) {
    this.featureSymbol = featureModelSymbol;
    this.solver = solver;
    trafos.add(new BasicTrafo());
    trafos.add(new RootFeatureSelected());
    trafos.add(new ComplexConstraint2FZN());
  }

  public FeatureModelAnalysisTool(FeatureDiagramSymbol featureSymbol) {
    this(featureSymbol, new ChocoSolver());

  }

  public void addFeatureModelTrafo(FeatureModel2FlatZincModelTrafo trafo) {
    trafos.add(trafo);
  }

  public void addComplexConstraint(ASTExpression expression) {
    expressions.add(expression);
  }

  public void addAllComplexConstraints(Collection<ASTExpression> expressions) {
    this.expressions.addAll(expressions);
  }

  public FeatureDiagramSymbol getFeatureSymbol() {
    return featureSymbol;
  }

  public void setFeatureSymbol(FeatureDiagramSymbol featureSymbol) {
    this.featureSymbol = featureSymbol;
  }

  public void addAnalysis(Analysis analysis) {
    this.analyses.add(analysis);
  }

  public void performAnalyses() {
    solver.setFeatureDiagrammName(featureSymbol.getName());
    analyses.forEach(
        analysis -> {
          FZNModelBuilder modelPrinter = analysis.getModelBuilder();
          modelPrinter.addAllFeatureModelFZNTrafos(trafos);
          modelPrinter.buildFlatZincModel(featureSymbol);
          analysis.setFeatureModel(featureSymbol);
          String s = modelPrinter.getFlatZincModel().print();
          analysis.perform(solver.solve(s, getAllFeatureNames(), modelPrinter.isAllSolutions()));
        }
    );
  }

  private List<String> getAllFeatureNames() {
    return featureSymbol.getAllFeatures().stream().map(FeatureSymbol::getName)
        .collect(Collectors.toList());
  }
}
