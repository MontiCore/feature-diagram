package tool.util;

import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramSymbolVisitor;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VariableDeterminator implements FeatureDiagramSymbolVisitor {

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
