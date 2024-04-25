/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFCElement;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import java.io.IOException;
import java.util.*;

public class CDxFDConformanceUtil {

  public static ASTFDCompilationUnit loadAndCheckFD(String path) {
    FeatureDiagramTool tool = new FeatureDiagramTool();

    // parse the fd-model-file
    ASTFDCompilationUnit ast = tool.parse(path);

    // create the symbol table
    IFeatureDiagramArtifactScope as;
    as = tool.createSymbolTable(ast);
    ast.setEnclosingScope(as);

    // check COCOs
    tool.runDefaultCoCos(ast);
    return ast;
  }

  public static ASTFCCompilationUnit loadAndCheckFc(String path) {
    FeatureConfigurationTool tool = new FeatureConfigurationTool();
    ASTFCCompilationUnit fc = tool.parse(path);

    IFeatureConfigurationArtifactScope as = tool.createSymbolTable(fc);
    fc.setEnclosingScope(as);

    tool.runDefaultCoCos(fc);
    return fc;
  }

  public static List<String> collectFeaturesNames(ASTFCCompilationUnit fc) {
    List<String> names = new ArrayList<>();

    for (ASTFCElement fce : fc.getFeatureConfiguration().getFCElementList()) {
      if (fce instanceof ASTFeatures) {
        ASTFeatures features = (ASTFeatures) fce;
        names.addAll(features.getNameList());
      }
    }
    return names;
  }

  public static List<String> collectAssocAndTypeNames(ASTCDCompilationUnit cd) {
    List<String> names = new ArrayList<>();

    cd.getCDDefinition().getCDClassesList().forEach(t -> names.add(t.getName()));
    cd.getCDDefinition().getCDInterfacesList().forEach(t -> names.add(t.getName()));
    cd.getCDDefinition().getCDEnumsList().forEach(t -> names.add(t.getName()));
    cd.getCDDefinition()
        .getCDAssociationsList()
        .forEach(
            assoc -> {
              if (assoc.isPresentName()) {
                names.add(assoc.getName());
              }
            });

    return names;
  }

  public static ASTCDCompilationUnit loadAndCheckCD(String path) {
    Optional<ASTCDCompilationUnit> cd;
    try {

      cd = CD4CodeMill.parser().parseCDCompilationUnit(path);
      if (cd.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(cd.get());
        cd.get().accept(new CD4CodeSymbolTableCompleter(cd.get()).getTraverser());
        return cd.get();
      } else {
        fail("Could not parse CDs.");
        return null;
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
    return null;
  }
}
