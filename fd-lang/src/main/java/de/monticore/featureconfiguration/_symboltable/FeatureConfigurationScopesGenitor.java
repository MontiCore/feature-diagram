/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._symboltable;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static de.monticore.featurediagram._symboltable.FeatureModelImporter.loadFeatureModelSymbol;

/**
 * This class builds up the symbols and scopes from an AST of an FD model.
 */
public class FeatureConfigurationScopesGenitor
    extends FeatureConfigurationScopesGenitorTOP {

  protected FeatureConfigurationSymbol fcSym;

  /**
   * Create the symbl table for a passed AST of an FC model.
   *
   * @param rootNode
   * @return
   */
  @Override public IFeatureConfigurationArtifactScope createFromAST(ASTFCCompilationUnit rootNode) {
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";
    handleImportStatements(rootNode);
    IFeatureConfigurationArtifactScope artifactScope = super.createFromAST(rootNode);
    artifactScope.setPackageName(packageName);
    return artifactScope;
  }

  /**
   * FC models can have a single import statement to import the FD model that they refer to.
   * If such an import statement exists, this method processes it by transforming the
   * fdName in the AST from an unqualified name to a qualified name.
   *
   * @param rootNode
   */
  public static void handleImportStatements(ASTFCCompilationUnit rootNode) {
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

  /**
   * for each name of a selected feature, resolve the FeatureSymbol
   *
   * @param node
   */
  @Override
  public void visit(ASTFeatures node) {
    super.visit(node);
    FeatureDiagramSymbol fd = fcSym.getFeatureDiagram();
    if (null != fd) {
      //to identify symbols that could not be found
      List<String> featureNameList = new ArrayList<>(node.getNameList());
      for (FeatureSymbol symbol : fd.getAllFeatures()) {
        if (featureNameList.contains(symbol.getName())) {
          featureNameList.remove(symbol.getName());
          fcSym.addSelectedFeatures(symbol);
        }
      }
      for (String name : featureNameList) {
        Log.error("0xFC001 The selected Feature " + name + " does not exist in Feature Model "
            + fd.getFullName());
      }
    }
  }

  /**
   * For the qualified name of the feature diagram that this FC model refers to, resolve the
   * FeatureDiagramSymbol.
   *
   * @param node
   */
  @Override
  public void visit(ASTFeatureConfiguration node) {
    super.visit(node);
    fcSym = node.getSymbol();
    FeatureDiagramSymbol fdSym = loadFeatureModelSymbol(node.getFdName(), node.getName());
    fcSym.setFeatureDiagram(fdSym);
  }

}
