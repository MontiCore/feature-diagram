/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._symboltable.*;

public interface FeatureDiagramVisitor extends FeatureDiagramVisitorTOP{

  @Override
  default void traverse(FeatureSymbol node) {
    node.getChildrenList().forEach(child->child.accept(getRealThis()));
  }

  default void handle(FeatureGroup node){
    getRealThis().visit(node);
    getRealThis().traverse(node);
    getRealThis().endVisit(node);
  }

  default void handle(AndGroup node){
    getRealThis().visit(node);
    getRealThis().traverse(node);
    getRealThis().endVisit(node);
  }

  default void handle(OrGroup node){
    getRealThis().visit(node);
    getRealThis().traverse(node);
    getRealThis().endVisit(node);
  }

  default void handle(XOrGroup node){
    getRealThis().visit(node);
    getRealThis().traverse(node);
    getRealThis().endVisit(node);
  }

  default void handle(CardinalityGroup node){
    getRealThis().visit(node);
    getRealThis().traverse(node);
    getRealThis().endVisit(node);
  }

  default void visit(FeatureGroup node){}
  default void visit(AndGroup node){}
  default void visit(OrGroup node){}
  default void visit(XOrGroup node){}
  default void visit(CardinalityGroup node){}

  default void endVisit(FeatureGroup node){}
  default void endVisit(AndGroup node){}
  default void endVisit(OrGroup node){}
  default void endVisit(XOrGroup node){}
  default void endVisit(CardinalityGroup node){}

  default void traverse(FeatureGroup node){}
}
