/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramCLI;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._ast.ASTFeatureTreeRule;
import de.monticore.featurediagram._ast.ASTGroupPart;

import java.util.Deque;

import static de.monticore.featurediagram._symboltable.FeatureModelImporter.importFD;

/**
 * This class builds up the symbols and scopes from an AST of an FD model.
 */
public class FeatureDiagramScopesGenitor extends FeatureDiagramScopesGenitorTOP {

  protected FeatureDiagramCLI fdTool = new FeatureDiagramCLI();

  public FeatureDiagramScopesGenitor() {
  }

  public FeatureDiagramScopesGenitor(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureDiagramScopesGenitor(Deque<? extends IFeatureDiagramScope> scopeStack) {
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
    handleImportStatements(rootNode);
    IFeatureDiagramArtifactScope artifactScope = super.createFromAST(rootNode);
    artifactScope.setPackageName(packageName);
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
   * @param ast
   */
  protected void handleImportStatements(ASTFDCompilationUnit ast) {
    ASTFeatureDiagram currentFD = ast.getFeatureDiagram();
    ASTFeatureDiagram importedFD = importFD(ast.getMCImportStatementList(), currentFD.getName());
    // add all features and cross-tree constraints of imported FD to the current FD
    if(null != importedFD){
      currentFD.addAllFDElements(importedFD.getFDElementList());
    }
  }

  /**
   * creates a FeatureSymbol on the first occurrence of a feature name in the current model
   * and adds this symbol to the Feature Diagram's scope
   *
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
