/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram._symboltable.serialization;

import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.symboltable.serialization.JsonPrinter;
import featurediagram._symboltable.FeatureDiagramScope;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP {

  public FeatureDiagramSymbolTablePrinter() {
  }

  public FeatureDiagramSymbolTablePrinter(
      JsonPrinter printer) {
    super(printer);
  }

  @Override protected void serializeFeatureDiagram(FeatureDiagramSymbol node) {
    getJsonPrinter().beginArray("features");
    for(FeatureSymbol s : node.getAllFeatures()){
      getJsonPrinter().value(s.getName());
    }
    getJsonPrinter().endArray();
  }

  @Override public void serializeLocalSymbols(IFeatureDiagramScope node) {
    // only store feature diagram symbols in the usual way. other symbols are omitted
    if (!node.getLocalFeatureDiagramSymbols().isEmpty()) {
      printer.beginArray("featureDiagramSymbols");
      node.getLocalFeatureDiagramSymbols().stream().forEach(s -> s.accept(getRealThis()));
      printer.endArray();
    }
  }

}
