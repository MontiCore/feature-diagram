/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This handwritten symbol table printer extends the generated one, because the serialization
 * strategy for FeatureSymbols deviates from the generated strategy. All FeatureSymbols are stored
 * as a list of feature names that are a member of a stored FeatureDiagramSymbol.
 */
public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP {

  public FeatureDiagramSymbolTablePrinter() {
  }

  public FeatureDiagramSymbolTablePrinter(
      JsonPrinter printer) {
    super(printer);
  }

  @Override protected void serializeAdditionalFeatureDiagramSymbolAttributes(FeatureDiagramSymbol node) {
    printer.array("features", node.getAllFeatures(), f->("\""+f.getName()+"\""));
  }

  @Override public void traverse(IFeatureDiagramScope node) {
    // only store feature diagram symbols in the usual way. other symbols are omitted
    if (!node.getLocalFeatureDiagramSymbols().isEmpty()) {
      printer.beginArray("featureDiagramSymbols");
      node.getLocalFeatureDiagramSymbols().stream().forEach(s -> s.accept(getRealThis()));
      printer.endArray();
    }
  }

  public  void traverse (FeatureDiagramSymbol node)  {
    //do not traverse spanned scope
  }

}
