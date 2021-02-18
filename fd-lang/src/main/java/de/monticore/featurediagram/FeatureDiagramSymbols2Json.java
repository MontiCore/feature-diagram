///* (c) https://github.com/MontiCore/monticore */
//
//package de.monticore.featurediagram._symboltable;
//
//import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
//import de.monticore.symboltable.serialization.JsonPrinter;
//
///**
// * This handwritten symbol table printer extends the generated one, because the serialization
// * strategy for FeatureSymbols deviates from the generated strategy. All FeatureSymbols are stored
// * as a list of feature names that are a member of a stored FeatureDiagramSymbol.
// */
//public class FeatureDiagramSymbols2Json extends FeatureDiagramSymbols2JsonTOP {
//
//  public FeatureDiagramSymbols2Json() {
//  }
//
//  public FeatureDiagramSymbols2Json(
//      FeatureDiagramTraverser traverser,
//      JsonPrinter printer) {
//    super(traverser, printer);
//  }
//
//  @Override public void traverse(IFeatureDiagramArtifactScope node) {
//    // only store feature diagram symbols in the usual way. other symbols are omitted
//    for (FeatureDiagramSymbol s : node.getLocalFeatureDiagramSymbols()) {
//      s.accept(getRealThis());
//    }
//  }
//
//  public void traverse(FeatureDiagramSymbol node) {
//    //do not traverse spanned scope
//  }
//
//}
