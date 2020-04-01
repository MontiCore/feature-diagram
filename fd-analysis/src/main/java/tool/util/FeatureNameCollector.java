package tool.util;


import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramSymbolVisitor;

import java.util.ArrayList;
import java.util.List;

public class FeatureNameCollector implements FeatureDiagramSymbolVisitor {

  List<String> names = new ArrayList<>();

  @Override
  public void visit(FeatureSymbol symbol) {
    names.add(symbol.getName());
  }

  public List<String> getNames(){
    return names;
  }
}
