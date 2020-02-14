/* (c) https://github.com/MontiCore/monticore */

package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import featurediagram._ast.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THIS CLASS IS CURRENTLY UNNECESSARY TODO check if needed after review BR
 * Extends the generated symbol table creator by adding a featuresymbol of the name of the first feature rule.
 * This is impoliclty set as root feature.
 */
public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

  protected Map<String, List<FeatureGroup>> groups;

  protected List<FeatureSymbol> featureSymbols;

  public FeatureDiagramSymbolTableCreator(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
    this.groups = new HashMap<>();
    this.featureSymbols = new ArrayList<>();
  }

  public FeatureDiagramSymbolTableCreator(Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
    this.groups = new HashMap<>();
    this.featureSymbols = new ArrayList<>();
  }

  @Override public FeatureDiagramArtifactScope createFromAST(ASTFDCompilationUnit rootNode) {
    List<ImportStatement> importStatements = rootNode
        .getMCImportStatementList().stream()
        .map(i -> new ImportStatement(i.getQName(), i.isStar()))
        .collect(Collectors.toList());
    String packageName = rootNode.isPresentPackage()?rootNode.getPackage().toString():"";

    FeatureDiagramArtifactScope artifactScope = FeatureDiagramSymTabMill
        .featureDiagramArtifactScopeBuilder()
        .addAllImports(importStatements)
        .setPackageName(packageName)
        .setEnclosingScopeAbsent()
        .build();

    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  //  protected ASTFeatureTreeRule firstRule; //TODO check if needed after review BR

  //  @Override public void visit(ASTFeatureTreeRule node) {  //TODO check if needed after review BR
  //    SourcePosition newPos = node.get_SourcePositionStart();
  //    super.visit(node);
  //    //set new firstRule, if it is the first rule in the model visited so far
  //    if (firstRule == null || newPos.compareTo(firstRule.get_SourcePositionStart()) < 0) {
  //      firstRule = node;
  //    }
  //  }

  //  @Override public void endVisit(ASTFeatureDiagram node) {  //TODO check if needed after review BR
  //    if (firstRule != null) {
  //      ASTFeature astFeature = FeatureDiagramMill.featureBuilder()
  //          .setOptional(true)
  //          .setName(firstRule.getName())
  //          .build();
  //      featurediagram._symboltable.FeatureSymbol symbol = create_Feature(astFeature);
  //      initialize_Feature(symbol, astFeature);
  //      addToScopeAndLinkWithNode(symbol, astFeature);
  //    }
  //  }

  /**
   * This method is overriden to set the root feature as attribute
   * @param symbol
   * @param ast
   */
  @Override protected void initialize_FeatureDiagram(FeatureDiagramSymbol symbol,
      ASTFeatureDiagram ast) {
    List<ASTRootFeature> roots = ast.getFDElementList().stream()
        .filter(e -> e instanceof ASTRootFeature)
        .map(e -> (ASTRootFeature) e)
        .collect(Collectors.toList());
    if (1 == roots.size()) {
      FeatureSymbolLoader rootLoader = FeatureDiagramSymTabMill
          .featureSymbolLoaderBuilder()
          .setEnclosingScope(this.getCurrentScope().get())
          .setName(roots.get(0).getFeature().getName())
          .build();
      symbol.setRootFeature(rootLoader);
    }
  }

  @Override protected void initialize_Feature(FeatureSymbol symbol, ASTFeature ast) {
    //store all feature symbols to a list, to set their groups in the endVisit of the FD
    featureSymbols.add(symbol);
  }

  @Override public void endVisit(ASTFeatureDiagram node) {
    super.endVisit(node);
    for(FeatureSymbol symbol : featureSymbols){
      if(groups.containsKey(symbol.getName())){
        symbol.setChildrenList(groups.get(symbol.getName()));
      }
      else{
        symbol.setChildrenList(new ArrayList<>());
      }
    }
  }

  @Override public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    FeatureSymbolLoader parent = createFeatureSymbolLoader(node.getName());
    List<FeatureSymbolLoader> children = new ArrayList<>();
    for (ASTFeature child : node.getFeatureGroup().getFeatureList()) {
      FeatureSymbolLoader fsl = createFeatureSymbolLoader(child.getName());
      children.add(fsl);
    }
    FeatureGroup.GroupKind kind = getGroupKind(node.getFeatureGroup());
    FeatureGroup group = new FeatureGroup(parent, children, kind);
    putFeatureGroup(group);
  }

  protected void putFeatureGroup(FeatureGroup group) {
    String parentName = group.getParent().getName();
    if (!groups.containsKey(parentName)) {
      groups.put(parentName, new ArrayList<>());
    }
    groups.get(parentName).add(group);
  }

  protected FeatureSymbolLoader createFeatureSymbolLoader(String featureName) {
    return FeatureDiagramSymTabMill
        .featureSymbolLoaderBuilder()
        .setEnclosingScope(getCurrentScope().get())
        .setName(featureName)
        .build();
  }

  protected FeatureGroup.GroupKind getGroupKind(ASTFeatureGroup featureGroup) {
    if (featureGroup instanceof ASTOrGroup) {
      return FeatureGroup.GroupKind.OR;
    }
    else if (featureGroup instanceof ASTXorGroup) {
      return FeatureGroup.GroupKind.XOR;
    }
    else if (featureGroup instanceof ASTAndGroup) {
      return FeatureGroup.GroupKind.AND;
    }
    else if (featureGroup instanceof ASTCardinalizedGroup) {
      return FeatureGroup.GroupKind.CARDINALITY;
    }
    else {
      Log.error("0xTODO Unknown feature group kind '" + featureGroup.getClass().getName() + "'");
      return null;
    }
  }
}
