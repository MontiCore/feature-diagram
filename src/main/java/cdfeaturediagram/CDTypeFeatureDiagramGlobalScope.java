package cdfeaturediagram;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.io.paths.ModelPath;
import featurediagram._symboltable.FeatureDiagramGlobalScope;
import featurediagram._symboltable.FeatureDiagramLanguage;

public class CDTypeFeatureDiagramGlobalScope extends FeatureDiagramGlobalScope {

  public CDTypeFeatureDiagramGlobalScope(ModelPath modelPath,
      FeatureDiagramLanguage language) {
    super(modelPath, language);
    CD4AnalysisGlobalScope gs = new CD4AnalysisGlobalScope(modelPath, new CD4AnalysisLanguage());
    CDTypeSymbolResolvingDelegate red = new CDTypeSymbolResolvingDelegate(gs);
    this.addAdaptedFeatureSymbolResolvingDelegate(red);
  }
}
