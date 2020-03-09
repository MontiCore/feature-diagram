/* (c) https://github.com/MontiCore/monticore */

package featurediagram._symboltable.serialization;

import featurediagram._symboltable.FeatureGroup;
import featurediagram._symboltable.FeatureSymbolLoader;
import featurediagram._symboltable.GroupKind;

import java.util.List;

public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP {

  @Override protected void serializeFeatureDiagramRootFeature(FeatureSymbolLoader rootFeature) {
    printer.member("rootFeature", rootFeature.getName());
  }

  @Override protected void serializeFeatureChildren(List<FeatureGroup> children) {
    //parent is implicitly given through containment in stored symtab
    printer.beginArray("children");
    for (FeatureGroup group : children) {
      printer.beginObject();
      printer.member("kind", group.getKind().name());
      if (GroupKind.CARDINALITY == group.getKind()) {
        printer.member("min", group.getMin());
        printer.member("max", group.getMax());
      }
      printer.beginArray("members");
      for (FeatureSymbolLoader member : group.getMembers()) {
        printer.value(member.getName());
      }
      printer.endArray();
      printer.endObject();
    }
    printer.endArray();
  }

}