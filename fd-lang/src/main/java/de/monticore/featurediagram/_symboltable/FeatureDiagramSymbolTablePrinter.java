/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Collection;
import java.util.function.Function;

public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP {

  public FeatureDiagramSymbolTablePrinter() {
  }

  public FeatureDiagramSymbolTablePrinter(
      JsonPrinter printer) {
    super(printer);
  }

  @Override protected void serializeAdditionalFeatureDiagramSymbolAttributes(
      FeatureDiagramSymbol node) {
//    getJsonPrinter().beginArray("features");
//    for (FeatureSymbol s : node.getAllFeatures()) {
//      getJsonPrinter().value(s.getName());
//    }
//    getJsonPrinter().endArray();
    printArray("features", node.getAllFeatures(), FeatureSymbol::getName);
  }

  public <T> void printArray(String name, Collection<T> values, Function<T,String> printValue){
    getJsonPrinter().beginArray(name);
    for (T t : values) {
      getJsonPrinter().value(printValue.apply(t));
    }
    getJsonPrinter().endArray();
  }


  @Override public void traverse(IFeatureDiagramScope node) {
    // only store feature diagram symbols in the usual way. other symbols are omitted
    if (!node.getLocalFeatureDiagramSymbols().isEmpty()) {
      printer.beginArray("featureDiagramSymbols");
      node.getLocalFeatureDiagramSymbols().stream().forEach(s -> s.accept(getRealThis()));
      printer.endArray();
    }
  }

}
