package de.monticore.fd.conformance.loader;

import com.microsoft.z3.Context;
import de.monticore.fd.conformance.fdmapping.FDMappingTool;
import de.monticore.fd.conformance.fdmapping._ast.ASTFDMapping;
import de.monticore.fd.conformance.fdmapping._symboltable.IFDMappingArtifactScope;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._symboltable.IFeatureDiagramArtifactScope;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public class FDLoader {
  public static ASTFDMapping loadAndCheckMapping(
      String refFDPath, String conFDPath, String MapPath) {
    FeatureDiagramTool fdTool = new FeatureDiagramTool();
    ASTFDCompilationUnit refFD = fdTool.parse(refFDPath);
    ASTFDCompilationUnit conFD = fdTool.parse(conFDPath);

    FDMappingTool tool = new FDMappingTool();

    // parse
    ASTFDMapping mapping = tool.parse(MapPath);
    Assertions.assertNotNull(mapping);

    IFDMappingArtifactScope scope = tool.createSymbolTable(mapping);
    mapping.setEnclosingScope(scope);
    tool.runDefaultCoCos(mapping);

    if (!checkMapping(refFD, conFD, mapping)) {
      Log.error("The mapping is not Valid");
    }

    return mapping;
  }

  private static boolean checkMapping(
      ASTFDCompilationUnit refFD, ASTFDCompilationUnit conFD, ASTFDMapping mapping) {

    for (String name : mapping.getConcreteFeatures()) {
      if (!conFD.getFeatureDiagram().getAllFeatures().contains(name)) {
        Log.error(
            "The mapping contains the Feature "
                + "["
                + name
                + "]"
                + " at a left side of a mapping-rule"
                + " This feature must exist in the concrete model ");
        return false;
      }
    }

    for (String name : mapping.getReferenceFeatures()) {
      if (!refFD.getFeatureDiagram().getAllFeatures().contains(name)) {
        Log.error(
            "The mapping contains the Feature "
                + "["
                + name
                + "]"
                + " at a right side. of a mapping-rule"
                + " This feature must exist in the reference model ");
        return false;
      }
    }

    return true;
  }

  public static ASTFDCompilationUnit loadAndCheckFD(String path) {
    FeatureDiagramTool tool = new FeatureDiagramTool();

    // parse the fd-model-file
    ASTFDCompilationUnit ast = tool.parse(path);

    // create the symbol table
    IFeatureDiagramArtifactScope as = null;
    as = tool.createSymbolTable(ast);
    ast.setEnclosingScope(as);

    // check COCOs
    tool.runDefaultCoCos(ast);
    return ast;
  }

  public static Context buildContext() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    return new Context(cfg);
  }
}
