/* (c) https://github.com/MontiCore/monticore */
package tool.util;

import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VariableDeterminator implements FeatureDiagramVisitor {

  List<Variable> variables = new ArrayList<>();

  Stack<String> features = new Stack<>();

  public List<Variable> getVariables() {
    return variables;
  }

  @Override
  public void visit(FeatureSymbol symbol) {
    features.push(symbol.getName());
    variables.add(new Variable(symbol.getName(), Variable.Type.INT, ""));
  }

  @Override
  public void endVisit(FeatureSymbol symbol) {
    features.pop();
  }
}
