/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.inc.association.CompAssocIncStrategy;
import de.monticore.cdconformance.inc.association.EqNameAssocIncStrategy;
import de.monticore.cdconformance.inc.association.STNamedAssocIncStrategy;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cdmatcher.MatchCDAssocsBySrcNameAndTgtRole;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class CDxFDConformance {

  /***
   * Check the conformance of a concrete class diagram to a reference class diagram, taking into account optional elements.
   * Optional elements of the reference class diagram are specified using a feature diagram.
   *
   * @param refCD the reference class diagram.
   * @param conCD the concrete class diagram.
   * @param refFD the feature diagram tha specify optional elements in the reference class diagram.
   * @param mapping the name of the mapping of element between reference and concrete class diagram.
   * @param params the conformance parameters.
   * @return a boolean flag that indicates whether the concrete model conforms.
   */
  public static boolean checkConformance(
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      ASTFDCompilationUnit refFD,
      String mapping,
      Set<CDConfParameter> params) {
    String logger = CDxFDConformance.class.getName();

    checkFDValidity(refFD, refCD);

    // build incarnation Strategies form the conformance params
    CompTypeIncStrategy typeInc = buildTypeIncStrategy(params, refCD, mapping);
    CompAssocIncStrategy assocInc = buildAssocIncStrategy(params, refCD, conCD, typeInc, mapping);

    // build a configuration form the concrete CD and the incarnation mapping
    ASTFCCompilationUnit fc = cd2FConfiguration(typeInc, assocInc, conCD, refFD);

    // check the validity of the configuration against the reference feature diagram.
    if (!FCValidator.checkFcValidity(refFD, fc)) {
      Log.info("The class diagram configuration do not match the class Feature diagram", logger);
      return false;
    }

    // mark all absent elements as optional in the reference class diagram
    List<String> potentialOptFeatures = refFD.getFeatureDiagram().getAllFeatures();
    potentialOptFeatures.removeIf(
        name -> CDxFDConformanceUtil.collectFeaturesNames(fc).contains(name));

    CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    AddOptionalStereotypeTrafo visitor = new AddOptionalStereotypeTrafo(potentialOptFeatures);
    traverser.add4CDInterfaceAndEnum(visitor);
    traverser.add4CDAssociation(visitor);
    traverser.add4CDBasis(visitor);
    refCD.accept(traverser);

    // check conformance
    CDConformanceChecker checker = new CDConformanceChecker(params);
    return checker.checkConformance(conCD, refCD, mapping);
  }

  private static CompTypeIncStrategy buildTypeIncStrategy(
      Set<CDConfParameter> params, ASTCDCompilationUnit refCD, String mapping) {
    CompTypeIncStrategy typeInc = new CompTypeIncStrategy(refCD, mapping);
    if (params.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
      typeInc.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    }
    if (params.contains(CDConfParameter.NAME_MAPPING)) {
      typeInc.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));
    }
    return typeInc;
  }

  private static CompAssocIncStrategy buildAssocIncStrategy(
      Set<CDConfParameter> params,
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      MatchingStrategy<ASTCDType> typeInc,
      String mapping) {
    CompAssocIncStrategy assocInc = new CompAssocIncStrategy(refCD, mapping);
    if (params.contains(CDConfParameter.STEREOTYPE_MAPPING)) {
      assocInc.addIncStrategy(new STNamedAssocIncStrategy(refCD, mapping));
    }
    if (params.contains(CDConfParameter.NAME_MAPPING)) {
      assocInc.addIncStrategy(new EqNameAssocIncStrategy(refCD, mapping));
    }
    if (params.contains(CDConfParameter.SRC_TARGET_ASSOC_MAPPING)) {
      assocInc.addIncStrategy(new MatchCDAssocsBySrcNameAndTgtRole(typeInc, conCD, refCD));
    }
    return assocInc;
  }

  /****
   * Transform a class diagram to a feature configuration. Features are associations and types.
   * The incarnation strategies help to map the concrete name to the reference names.
   */
  public static ASTFCCompilationUnit cd2FConfiguration(
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc,
      ASTCDCompilationUnit conCD,
      ASTFDCompilationUnit fd) {

    Set<String> features = new HashSet<>();

    for (ASTCDType type : conCD.getCDDefinition().getCDClassesList()) {
      resolveRefName(typeInc, type).map(features::add);
    }
    for (ASTCDType type : conCD.getCDDefinition().getCDInterfacesList()) {
      resolveRefName(typeInc, type).map(features::add);
    }
    for (ASTCDType type : conCD.getCDDefinition().getCDEnumsList()) {
      resolveRefName(typeInc, type).map(features::add);
    }
    for (ASTCDAssociation assoc : conCD.getCDDefinition().getCDAssociationsList()) {
      resolveRefName(assocInc, assoc).map(features::add);
    }

    features.removeIf(x -> !fd.getFeatureDiagram().getAllFeatures().contains(x));
    features.add(fd.getFeatureDiagram().getRootFeature());

    return FeatureConfigurationMill.fCCompilationUnitBuilder()
        .setFeatureConfiguration(
            FeatureConfigurationMill.featureConfigurationBuilder()
                .setFdName("CD")
                .setName("FD")
                .addFCElement(
                    FeatureConfigurationMill.featuresBuilder()
                        .setNamesList(new ArrayList<>(features))
                        .build())
                .build())
        .build();
  }
  /*** check if all the features in the reference feature diagram except the root
   *  are present in the reference class diagram
   *  */
  public static void checkFDValidity(ASTFDCompilationUnit fd, ASTCDCompilationUnit cd) {
    List<String> validFeaturesName = CDxFDConformanceUtil.collectAssocAndTypeNames(cd);
    List<String> allFeatures = new ArrayList<>(fd.getFeatureDiagram().getAllFeatures());
    allFeatures.remove(fd.getFeatureDiagram().getRootFeature());

    for (String name : allFeatures) {
      if (!validFeaturesName.contains(name)) {
        Log.error(
            "The feature "
                + name
                + " is not  present in the reference class diagram and is therefore not allowed");
      }
    }
  }

  public static Optional<String> resolveRefName(
      MatchingStrategy<ASTCDType> typeInc, ASTCDType con) {
    return typeInc.getMatchedElements(con).stream().findFirst().map(ASTCDType::getName);
  }

  public static Optional<String> resolveRefName(
      MatchingStrategy<ASTCDAssociation> assocInc, ASTCDAssociation con) {
    return assocInc.getMatchedElements(con).stream().findFirst().map(ASTCDAssociation::getName);
  }
}
