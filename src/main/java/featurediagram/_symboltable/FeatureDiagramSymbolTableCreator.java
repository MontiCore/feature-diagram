/* (c) https://github.com/MontiCore/monticore */

package featurediagram._symboltable;

import java.util.Deque;

/**
 * THIS CLASS IS CURRENTLY UNNECESSARY TODO check if needed after review BR
 * Extends the generated symbol table creator by adding a featuresymbol of the name of the first feature rule.
 * This is impoliclty set as root feature.
 */
public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

//  protected ASTFeatureTreeRule firstRule; //TODO check if needed after review BR

  public FeatureDiagramSymbolTableCreator(
      IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureDiagramSymbolTableCreator(
      Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
  }

//  @Override public void visit(ASTFeatureTreeRule node) {  //TODO check if needed after review BR
//    SourcePosition newPos = node.get_SourcePositionStart();
//    super.visit(node);
//    //set new firstRule, if it is the first rule in the model visited so far
//    if (firstRule == null || newPos.compareTo(firstRule.get_SourcePositionStart()) < 0) {
//      firstRule = node;
//    }
//  }

//  @Override public void endVisit(ASTFeatureDiagram node) {  //TODO check if needed after review BR
//    if (firstRule != null) {
//      ASTFeature astFeature = FeatureDiagramMill.featureBuilder()
//          .setOptional(true)
//          .setName(firstRule.getName())
//          .build();
//      featurediagram._symboltable.FeatureSymbol symbol = create_Feature(astFeature);
//      initialize_Feature(symbol, astFeature);
//      addToScopeAndLinkWithNode(symbol, astFeature);
//    }
//  }

}
