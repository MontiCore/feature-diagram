/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureTreeRule;
import de.monticore.featurediagram._ast.ASTGroupPart;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;

/**
 * This class builds up the symbols and scopes from an AST of an FD model.
 */
public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

  public FeatureDiagramSymbolTableCreator() {
  }

  public FeatureDiagramSymbolTableCreator(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureDiagramSymbolTableCreator(Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
  }

  /**
   * Create the symbl table for a passed AST of an FD model.
   *
   * @param rootNode
   * @return
   */
  @Override
  public IFeatureDiagramArtifactScope createFromAST(ASTFDCompilationUnit rootNode) {
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";
    IFeatureDiagramArtifactScope artifactScope = FeatureDiagramMill.artifactScope();
    artifactScope.setPackageName(packageName);
    putOnStack(artifactScope);
    handleImportStatements(rootNode);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * This method adds features on the right-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override
  public void visit(ASTGroupPart node) {
    super.visit(node);
    createFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method adds features on the left-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override
  public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    createFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method iterates over imported feature diagrams through import statements and
   * for each of these, adds all elements of the imported feature diagram to the AST of the
   * current feature diagram.
   *
   * @param rootNode
   */
  protected void handleImportStatements(ASTFDCompilationUnit rootNode) {
    for (ASTMCImportStatement i : rootNode.getMCImportStatementList()) {
      if (i.isStar()) {
        Log.error("0xFD132 Feature diagrams may not use stars '*' in import statements!");
        continue;
      }
      FeatureDiagramSymbol fd = FeatureDiagramMill.globalScope()
          .resolveFeatureDiagram(i.getQName()).orElse(null);
      if (null == fd) {
        Log.error("0xFD133 Cannot find imported feature diagram '" + i.getQName() + "' in '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        continue;
      }
      if (!fd.isPresentAstNode()) {
        Log.error("0xFD134 Cannot find AST of imported feature diagram '" + i.getQName() + "' in '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        continue;
      }
      rootNode.getFeatureDiagram().addAllFDElements(fd.getAstNode().getFDElementList());
    }
  }

  /**
   * creates a FeatureSymbol on the first occurrence of a feature name in the current model
   * and adds this symbol to the Feature Diagram's scope
   * @param name
   */
  protected void createFeatureSymbolOnFirstOccurrence(String name) {
    // if this feature name has already occured in the current feature model, stop
    if (!getCurrentScope().get().resolveFeatureLocally(name).isPresent()) {
      //otherwise, create new FeatureSymbol
      addToScope(FeatureDiagramMill
          .featureSymbolBuilder()
          .setName(name)
          .build());
    }
  }

}
