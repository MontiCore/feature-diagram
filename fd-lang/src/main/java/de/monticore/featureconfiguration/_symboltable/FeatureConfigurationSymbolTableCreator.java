/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._symboltable;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureConfigurationSymbolTableCreator
    extends FeatureConfigurationSymbolTableCreatorTOP {

  private List<FeatureSymbol> selectedSymbols = new ArrayList<>();

  private FeatureDiagramSymbol fd;

  public FeatureConfigurationSymbolTableCreator(IFeatureConfigurationScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureConfigurationSymbolTableCreator(
      Deque<? extends IFeatureConfigurationScope> scopeStack) {
    super(scopeStack);
  }

  @Override public FeatureConfigurationArtifactScope createFromAST(ASTFCCompilationUnit rootNode) {
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    FeatureConfigurationArtifactScope artifactScope = de.monticore.featureconfiguration.FeatureConfigurationMill
        .featureConfigurationArtifactScopeBuilder()
        .setImportList(new ArrayList<>())
        .setPackageName(packageName)
        .build();

    putOnStack(artifactScope);
    handleImportStatements(rootNode);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  public void handleImportStatements(ASTFCCompilationUnit rootNode) {
    List<ASTMCImportStatement> imports = rootNode.getMCImportStatementList();
    if (1 < imports.size()) {
      Log.error("0xFC010 The feature configuration '" + rootNode.getFeatureConfiguration().getName()
          + "' must not import more than one other model!");
    }
    else if (1 == imports.size()) {
      String imp = imports.get(0).getQName();
      String fdName = rootNode.getFeatureConfiguration().getFdName();
      if (Names.getSimpleName(imp).equals(fdName)) {
        rootNode.getFeatureConfiguration().setFdName(imp);
      }
      else if (imports.get(0).isStar()) {
        rootNode.getFeatureConfiguration().setFdName(Names.getQualifiedName(imp, fdName));
      }
      else {
        Log.error(
            "0xFC011 The feature configuration '" + rootNode.getFeatureConfiguration().getName()
                + "' contains the import statement '" + imp
                + "' that does not match the feature diagram '" + fdName + "'!");
      }
    }
  }

  @Override
  public void visit(ASTFeatures node) {
    super.visit(node);

    //to identify symbols that could not be found
    List<String> featureNameList = new ArrayList<>(node.getNameList());
    if (null != fd) {
      node.setNameList(
          node.getNameList().stream().map(s -> Names.getQualifiedName(fd.getFullName(), s))
              .collect(Collectors.toList()));

      for (FeatureSymbol symbol : fd.getAllFeatures()) {
        if (featureNameList.contains(symbol.getName())) {
          featureNameList.remove(symbol.getName());
          selectedSymbols.add(symbol);
        }
      }
      for (String name : featureNameList) {
        Log.error("0xFC001 The selected Feature " + name + " does not exist in Feature Model " + fd
            .getFullName());
      }
    }
  }

  @Override
  public void endVisit(ASTFeatureConfiguration node) {
    super.endVisit(node);
    node.getSymbol().setSelectedFeatureList(selectedSymbols);
    node.getSymbol().setFeatureDiagram(fd);
  }

  @Override
  public void visit(ASTFeatureConfiguration node) {
    super.visit(node);
    if (node.isPresentFdNameSymbol()) {
      fd = node.getFdNameSymbol();
    }
    else {
      Log.error(
          "0xFC002 The feature configuration `" + node.getName() + "` uses the feature model '"
              + node.getFdName() + "' that cannot be resolved!");
    }
  }

}
