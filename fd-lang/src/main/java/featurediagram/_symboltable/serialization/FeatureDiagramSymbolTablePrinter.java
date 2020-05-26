/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.monticore.symboltable.serialization.JsonPrinter;
import featurediagram._symboltable.*;
import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.List;

public class FeatureDiagramSymbolTablePrinter extends FeatureDiagramSymbolTablePrinterTOP implements FeatureDiagramVisitor {

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
      group.accept(this);
      printer.beginArray("members");
      for (FeatureSymbol member : group.getMembers()) {
        printer.value(member.getName());
      }
      printer.endArray();
      printer.endObject();
    }
    printer.endArray();
  }

  public void visit(AndGroup group){
    printer.member("kind", "AndGroup");
    printer.beginArray("optionals");
    group.getOptionalFeatures().forEach(feature -> printer.value(feature));
    printer.endArray();
  }

  public void visit(OrGroup group){
    printer.member("kind", "OrGroup");
  }

  public void visit(XOrGroup group){
    printer.member("kind", "XOrGroup");
  }

  public void visit(CardinalityGroup group){
    printer.member("kind", "CardinalityGroup");
    printer.member("min", group.getMin());
    printer.member("max", group.getMax());
  }
}
