/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.List;

public class FeatureConfigurationSymbolTablePrinter
    extends FeatureConfigurationSymbolTablePrinterTOP {

  public FeatureConfigurationSymbolTablePrinter() {
  }

  public FeatureConfigurationSymbolTablePrinter(
      JsonPrinter printer) {
    super(printer);
  }

  /**
   * serializes the feature diagram as qualified name in form of a JSON String
   * @param featureDiagram
   */
  @Override public void serializeFeatureConfigurationFeatureDiagram(
      FeatureDiagramSymbol featureDiagram) {
    getJsonPrinter().member("featureDiagram", featureDiagram.getFullName());
  }

  /**
   * serializes the list of feature symbols as JSON array of the (unqualified) feature names
   * as JSON Strings
   * @param selectedFeatures
   */
  @Override public void serializeFeatureConfigurationSelectedFeatures(
      List<FeatureSymbol> selectedFeatures) {
    getJsonPrinter().array("selectedFeatures", selectedFeatures,
        feature -> "\"" + feature.getName() + "\"");
  }
}
