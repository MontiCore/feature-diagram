/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.JsonPrinter;
import featurediagram._symboltable.AndGroup;
import featurediagram._symboltable.CardinalitiyGroup;
import featurediagram._symboltable.FeatureGroup;
import featurediagram._symboltable.FeatureSymbol;

import java.util.List;

public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP {

  public FeatureDiagramSymbolTablePrinter() {
    super();
  }

  public FeatureDiagramSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  @Override protected void serializeFeatureDiagramRootFeature(FeatureSymbol rootFeature) {
    printer.member("rootFeature", rootFeature.getName());
  }

  @Override protected void serializeFeatureChildren(List<FeatureGroup> children) {
    //parent is implicitly given through containment in stored symtab
    printer.beginArray("children");
    for (FeatureGroup group : children) {
      printer.beginObject();
      printer.member("kind", group.getClass().getName());
      if (group instanceof CardinalitiyGroup) {
        printer.member("min", group.getMin());
        printer.member("max", group.getMax());
      }
      if (group instanceof AndGroup){
        printer.beginArray("optionals");
        ((AndGroup) group).getOptionalFeatures().forEach(b->printer.value(b));
        printer.endArray();
      }
      printer.beginArray("members");
      for (FeatureSymbol member : group.getMembers()) {
        printer.value(member.getName());
      }
      printer.endArray();
      printer.endObject();
    }
    printer.endArray();
  }



}
