/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._symboltable.IFeatureDiagramScope;

public interface FeatureDiagramVisitor extends FeatureDiagramVisitorTOP{
  default public void handle(IFeatureDiagramScope scope){
    getRealThis().visit(scope);
    getRealThis().traverse(scope);
    getRealThis().endVisit(scope);
  }

  default public void visit(IFeatureDiagramScope node){}
  default public void endVisit(IFeatureDiagramScope node){}

  default public void traverse(IFeatureDiagramScope node){
    for (featurediagram._symboltable.FeatureSymbol s : node.getLocalFeatureSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.types.typesymbols._symboltable.TypeSymbol s : node.getLocalTypeSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.types.typesymbols._symboltable.FieldSymbol s : node.getLocalFieldSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.types.typesymbols._symboltable.TypeVarSymbol s : node.getLocalTypeVarSymbols()) {
      s.accept(getRealThis());
    }
    for (de.monticore.types.typesymbols._symboltable.MethodSymbol s : node.getLocalMethodSymbols()) {
      s.accept(getRealThis());
    }
    for (featurediagram._symboltable.FeatureDiagramSymbol s : node.getLocalFeatureDiagramSymbols()) {
      s.accept(getRealThis());
    }
  }
}
