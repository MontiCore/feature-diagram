/* (c) https://github.com/MontiCore/monticore */
package tool;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import featurediagram._ast.ASTConstraintExpression;
import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.analyses.Analysis;
import tool.solver.ISolver;
import tool.solver.choco.ChocoSolver;
import tool.transform.FZNModelBuilder;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.trafos.BasicTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;
import tool.transform.trafos.RootFeatureSelected;
import tool.util.FeatureNameCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatureModelAnalysisTool {

  private FeatureDiagramSymbol featureSymbol;

  private List<ASTConstraintExpression> expressions = new ArrayList<>();

  private List<Analysis> analyses = new ArrayList<>();

  private ISolver solver;

  private List<FeatureModel2FlatZincModelTrafo> trafos = new ArrayList<>();

  public FeatureModelAnalysisTool(FeatureDiagramSymbol featureModelSymbol, ISolver solver) {
    this.featureSymbol = featureModelSymbol;
    this.solver = solver;
    trafos.add(new BasicTrafo());
    trafos.add(new RootFeatureSelected());
    trafos.add(new ComplexConstraint2FZN(expressions));
  }

  public FeatureModelAnalysisTool(FeatureDiagramSymbol featureSymbol) {
    this(featureSymbol, new ChocoSolver());

  }

  public void addFeatureModelTrafo(FeatureModel2FlatZincModelTrafo trafo) {
    trafos.add(trafo);
  }

  public void addComplexConstraint(ASTConstraintExpression expression) {
    expressions.add(expression);
  }

  public void addAllComplexConstraints(Collection<ASTConstraintExpression> expressions) {
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
    analyses.forEach(
        analysis -> {
          FZNModelBuilder modelPrinter = analysis.getModelBuilder();
          modelPrinter.addAllFeatureModelFZNTrafos(trafos);
          modelPrinter.buildFlatZincModel(featureSymbol);
          analysis.setFeatureModel(featureSymbol);
          String s = modelPrinter.getFlatZincModel().print();
          System.out.println(s);
          analysis.perform(solver.solve(s, getAllFeatureNames(), modelPrinter.isAllSolutions()));
        }
    );
  }

  private List<String> getAllFeatureNames() {
    FeatureNameCollector namesCollector = new FeatureNameCollector();
    featureSymbol.accept(namesCollector);
    return namesCollector.getNames();
  }
}