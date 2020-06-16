/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._ast.ASTFeatureDiagram;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubFeatureFinder implements FeatureDiagramVisitor{
  private Set<String> subfeatures = new HashSet<>();

  public List<String> getAllSubfeatures(FeatureSymbol symbol){
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) symbol.getEnclosingScope().getSpanningSymbol().getAstNode();
    subfeatures.add(symbol.getName());
    int size = 0;
    while (size != subfeatures.size()) {
      size = subfeatures.size();
      featureDiagram.accept(this);
    }
    subfeatures.remove(symbol.getName());
    return new ArrayList<>(subfeatures);
  }

  public void visit(ASTFeatureTreeRule rule){
    if(subfeatures.contains(rule.getName())){
      rule.getFeatureGroup().streamGroupParts().forEach( groupPart ->
        subfeatures.add(groupPart.getName())
      );
    }
  }

}
