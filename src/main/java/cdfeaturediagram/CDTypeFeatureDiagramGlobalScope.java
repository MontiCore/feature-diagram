/* (c) https://github.com/MontiCore/monticore */
package cdfeaturediagram;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.io.paths.ModelPath;
import featurediagram._symboltable.FeatureDiagramGlobalScope;
import featurediagram._symboltable.FeatureDiagramLanguage;
import featurediagram._symboltable.IFeatureDiagramScope;

public class CDTypeFeatureDiagramGlobalScope extends FeatureDiagramGlobalScope {

  public CDTypeFeatureDiagramGlobalScope(ModelPath modelPath) {
    super(modelPath, new FeatureDiagramLanguage());
    CD4AnalysisGlobalScope gs = new CD4AnalysisGlobalScope(modelPath, new CD4AnalysisLanguage());
    this.addAdaptedFeatureSymbolResolvingDelegate(new CDTypeSymbolResolvingDelegate(gs));
  }

  @Override public IFeatureDiagramScope getEnclosingScope() {
    return null;
  }
}
