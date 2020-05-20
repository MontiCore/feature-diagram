/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

  protected Map<String, List<FeatureGroup>> groups;

  protected Map<String, FeatureSymbol> featureSymbols;

  private FeatureSymbol currentSymbol;

  public FeatureDiagramSymbolTableCreator(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
    this.groups = new HashMap<>();
    this.featureSymbols = new HashMap<>();
  }

  public FeatureDiagramSymbolTableCreator(Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
    this.groups = new HashMap<>();
    this.featureSymbols = new HashMap<>();
  }

  @Override
  public FeatureDiagramArtifactScope createFromAST(ASTFDCompilationUnit rootNode) {
    List<ImportStatement> importStatements = rootNode
            .getMCImportStatementList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList());
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    FeatureDiagramArtifactScope artifactScope = FeatureDiagramMill
            .featureDiagramArtifactScopeBuilder()
            .addAllImports(importStatements)
            .setPackageName(packageName)
            .setEnclosingScopeAbsent()
            .build();

    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void visit(ASTFeatureDiagram node) {
    super.visit(node);
    String root = node.getRootFeature();
    node.getSymbol().setRootFeature(getResolveOrCreateFeatureSymbol(root));
  }

  @Override
  public void endVisit(ASTFeatureDiagram node) {
    super.endVisit(node);
    for (FeatureSymbol symbol : featureSymbols.values()) {
      if (groups.containsKey(symbol.getName())) {
        symbol.setChildrenList(groups.get(symbol.getName()));
      } else {
        symbol.setChildrenList(new ArrayList<>());
      }
    }
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    currentSymbol = getResolveOrCreateFeatureSymbol(node.getName());
  }

  @Override
  public void visit(ASTAndGroup node) {
    super.visit(node);
    FeatureSymbol parent = currentSymbol;
    List<FeatureSymbol> children = new ArrayList<>();
    for (String child : node.getNameList()) {
      FeatureSymbol fs = getResolveOrCreateFeatureSymbol(child);
      children.add(fs);
    }
    putFeatureGroup(new AndGroup(parent, children, node.getOptionalList().stream().map(s -> "?".equals(s)).collect(Collectors.toList())));
  }

  @Override
  public void visit(ASTXorGroup node) {
    super.visit(node);
    FeatureSymbol parent = currentSymbol;
    List<FeatureSymbol> children = new ArrayList<>();
    for (String child : node.getNameList()) {
      FeatureSymbol fs = getResolveOrCreateFeatureSymbol(child);
      children.add(fs);
    }
    putFeatureGroup(new XOrGroup(parent, children));
  }

  @Override
  public void visit(ASTOrGroup node) {
    super.visit(node);
    FeatureSymbol parent = currentSymbol;
    List<FeatureSymbol> children = new ArrayList<>();
    for (String child : node.getNameList()) {
      FeatureSymbol fs = getResolveOrCreateFeatureSymbol(child);
      children.add(fs);
    }
    putFeatureGroup(new OrGroup(parent, children));
  }

  @Override
  public void visit(ASTCardinalizedGroup node) {
    super.visit(node);
    FeatureSymbol parent = currentSymbol;
    List<FeatureSymbol> children = new ArrayList<>();
    for (String child : node.getNameList()) {
      FeatureSymbol fs = getResolveOrCreateFeatureSymbol(child);
      children.add(fs);
    }
    putFeatureGroup(new CardinalitiyGroup(parent, children, node.getCardinality().getLowerBound(), node.getCardinality().getUpperBound()));
  }

  protected void putFeatureGroup(FeatureGroup group) {
    String parentName = group.getParent().getName();
    if (!groups.containsKey(parentName)) {
      groups.put(parentName, new ArrayList<>());
    }
    groups.get(parentName).add(group);
  }

  protected FeatureSymbol getResolveOrCreateFeatureSymbol(String name){
    Optional<FeatureSymbol> featureSymbol = Optional.ofNullable(featureSymbols.get(name));
    if(featureSymbol.isPresent()){
      return featureSymbol.get();
    }
    featureSymbol = getCurrentScope().get().resolveFeature(name);
    if(featureSymbol.isPresent()){
      featureSymbols.put(name, featureSymbol.get());
      return featureSymbol.get();
    }
    featureSymbol = Optional.of(FeatureDiagramMill.featureSymbolBuilder()
            .setEnclosingScope(getCurrentScope().get())
            .setName(name)
            .build());
    getCurrentScope().get().add(featureSymbol.get());
    featureSymbols.put(name, featureSymbol.get());
    return featureSymbol.get();
  }
}
