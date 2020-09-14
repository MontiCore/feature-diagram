/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial._symboltable;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbolTableCreator;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FeatureConfigurationPartialSymbolTableCreator
    extends FeatureConfigurationPartialSymbolTableCreatorTOP {

  public FeatureConfigurationPartialSymbolTableCreator(
      IFeatureConfigurationPartialScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureConfigurationPartialSymbolTableCreator(
      Deque<? extends IFeatureConfigurationPartialScope> scopeStack) {
    super(scopeStack);
  }

  @Override public IFeatureConfigurationPartialArtifactScope createFromAST(ASTFCCompilationUnit rootNode) {
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    IFeatureConfigurationPartialArtifactScope artifactScope = FeatureConfigurationPartialMill
        .featureConfigurationPartialArtifactScopeBuilder()
        .setImportsList(new ArrayList<>())
        .setPackageName(packageName)
        .build();

    putOnStack(artifactScope);
    FeatureConfigurationSymbolTableCreator.handleImportStatements(rootNode);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

}
