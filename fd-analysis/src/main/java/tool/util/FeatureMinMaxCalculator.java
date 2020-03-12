package tool.util;

import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramSymbolVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FeatureMinMaxCalculator implements FeatureDiagramSymbolVisitor {

  private Map<FeatureSymbol, Integer> min = new HashMap<>();
  private Map<FeatureSymbol, Integer> max = new HashMap<>();
  private Stack<FeatureSymbol> features= new Stack<>();

  public Map<FeatureSymbol, Integer> getMin() {
    return min;
  }

  public Map<FeatureSymbol, Integer> getMax() {
    return max;
  }

  @Override
  public void visit(FeatureSymbol symbol) {
    min.put(symbol, symbol.isIsOptional()? 0: 1);
    max.put(symbol, 1);
    features.push(symbol);
  }

  @Override
  public void endVisit(FeatureSymbol symbol) {
    features.pop();
  }

}
