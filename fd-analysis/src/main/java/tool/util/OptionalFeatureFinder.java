/* (c) https://github.com/MontiCore/monticore */
package tool.util;

import featurediagram._ast.ASTAndGroup;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.ArrayList;
import java.util.List;

public class OptionalFeatureFinder implements FeatureDiagramVisitor {
  private List<FeatureSymbol> optionalFeatures = new ArrayList<>();

  @Override
  public void visit(ASTAndGroup andGroup) {
    for(int i = 0; i < andGroup.sizeGroupParts(); i++){
      if(andGroup.getGroupPart(i).isOptional()){
        optionalFeatures.add(andGroup.getGroupPart(i).getNameSymbol());
      }
    }
  }

  public List<FeatureSymbol> getOptionalFeatures() {
    return optionalFeatures;
  }
}
