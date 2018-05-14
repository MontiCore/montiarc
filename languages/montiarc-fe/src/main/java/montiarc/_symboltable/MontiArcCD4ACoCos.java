/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.ebnf.AssociationNameLowerCase;
import de.monticore.umlcd4a.cocos.ebnf.AssociationNameNoConflictWithAttribute;
import de.monticore.umlcd4a.cocos.ebnf.AssociationOrderedCardinalityGreaterOne;
import de.monticore.umlcd4a.cocos.ebnf.AssociationQualifierAttributeExistsInTarget;
import de.monticore.umlcd4a.cocos.ebnf.AssociationQualifierOnCorrectSide;
import de.monticore.umlcd4a.cocos.ebnf.AssociationQualifierTypeExists;
import de.monticore.umlcd4a.cocos.ebnf.AssociationRoleNameLowerCase;
import de.monticore.umlcd4a.cocos.ebnf.AssociationRoleNameNoConflictWithAttribute;
import de.monticore.umlcd4a.cocos.ebnf.AssociationRoleNameNoConflictWithOtherRoleNames;
import de.monticore.umlcd4a.cocos.ebnf.AssociationSourceNotEnum;
import de.monticore.umlcd4a.cocos.ebnf.AssociationSourceTypeNotExternal;
import de.monticore.umlcd4a.cocos.ebnf.AssociationSrcAndTargetTypeExistChecker;
import de.monticore.umlcd4a.cocos.ebnf.AttributeNameLowerCase;
import de.monticore.umlcd4a.cocos.ebnf.AttributeOverriddenTypeMatch;
import de.monticore.umlcd4a.cocos.ebnf.AttributeTypeCompatible;
import de.monticore.umlcd4a.cocos.ebnf.AttributeTypeExists;
import de.monticore.umlcd4a.cocos.ebnf.AttributeUniqueInClassCoco;
import de.monticore.umlcd4a.cocos.ebnf.ClassExtendsOnlyClasses;
import de.monticore.umlcd4a.cocos.ebnf.ClassImplementOnlyInterfaces;
import de.monticore.umlcd4a.cocos.ebnf.CompositionCardinalityValid;
import de.monticore.umlcd4a.cocos.ebnf.DiagramNameUpperCase;
import de.monticore.umlcd4a.cocos.ebnf.EnumConstantsUnique;
import de.monticore.umlcd4a.cocos.ebnf.EnumImplementOnlyInterfaces;
import de.monticore.umlcd4a.cocos.ebnf.ExtendsNotCyclic;
import de.monticore.umlcd4a.cocos.ebnf.GenericParameterCountMatch;
import de.monticore.umlcd4a.cocos.ebnf.GenericTypeHasParameters;
import de.monticore.umlcd4a.cocos.ebnf.GenericsNotNested;
import de.monticore.umlcd4a.cocos.ebnf.InterfaceExtendsOnlyInterfaces;
import de.monticore.umlcd4a.cocos.ebnf.TypeNameUpperCase;
import de.monticore.umlcd4a.cocos.ebnf.TypeNoInitializationOfDerivedAttribute;
import de.monticore.umlcd4a.cocos.ebnf.UniqueTypeNames;
import de.monticore.umlcd4a.cocos.mcg.AssociationModifierCoCo;
import de.monticore.umlcd4a.cocos.mcg.AttributeNotAbstractCoCo;
import de.monticore.umlcd4a.cocos.mcg.ClassInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.EnumInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.InterfaceAttributesStaticCoCo;
import de.monticore.umlcd4a.cocos.mcg.InterfaceInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.ModifierNotMultipleVisibilitiesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AssociationEndModifierRestrictionCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AssociationNoStereotypesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AttributeModifierOnlyDerivedCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassModifierOnlyAbstractCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassNoConstructorsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoConstructorsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoModifierCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoAttributesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoModifierCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.StereoValueNoValueCoCo;
import montiarc.helper.CD4AModelLoader;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class MontiArcCD4ACoCos extends CD4ACoCos{

  @Override
  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    addEbnfCoCos(checker);
    addMcgCoCos(checker);
    addMcg2EbnfCoCos(checker);

    return checker;
  }
  
  
  private void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new DiagramNameUpperCase());
    checker.addCoCo(new UniqueTypeNames());
    checker.addCoCo(new TypeNameUpperCase());
    checker.addCoCo(new EnumConstantsUnique());
    checker.addCoCo(new AttributeUniqueInClassCoco());
    checker.addCoCo(new AssociationSourceTypeNotExternal());
    checker.addCoCo(new ExtendsNotCyclic());
    checker.addCoCo(new InterfaceExtendsOnlyInterfaces());
    checker.addCoCo(new ClassExtendsOnlyClasses());
    checker.addCoCo(new ClassImplementOnlyInterfaces());
    checker.addCoCo(new EnumImplementOnlyInterfaces());
    checker.addCoCo(new GenericsNotNested());
    checker.addCoCo(new GenericTypeHasParameters());
    checker.addCoCo(new GenericParameterCountMatch());
    //TODO allow java types as well
//    checker.addCoCo(new AttributeTypeCompatible());
    checker.addCoCo(new AttributeNameLowerCase());
    checker.addCoCo(new AttributeOverriddenTypeMatch());
    //TODO allow java types as well
//    checker.addCoCo(new AttributeTypeExists());
    checker.addCoCo(new CompositionCardinalityValid());
    checker.addCoCo(new AssociationQualifierTypeExists());
    checker.addCoCo(new AssociationQualifierAttributeExistsInTarget());
    checker.addCoCo(new AssociationQualifierOnCorrectSide());
    checker.addCoCo(new AssociationSourceNotEnum());
    checker.addCoCo(new AssociationOrderedCardinalityGreaterOne());
    checker.addCoCo(new AssociationNameLowerCase());
    checker.addCoCo(new AssociationRoleNameLowerCase());
    // This CoCo is temporary disabled as the association name does not need to be unique within a model.
    // Instead, it must be unique within a specific class hierarchy.
    // checker.addCoCo(new AssociationNameUnique());
    checker.addCoCo(new AssociationNameNoConflictWithAttribute());
    checker.addCoCo(new AssociationRoleNameNoConflictWithAttribute());
    checker.addCoCo(new AssociationRoleNameNoConflictWithOtherRoleNames());
    checker.addCoCo(new AssociationSrcAndTargetTypeExistChecker());
    checker.addCoCo(new TypeNoInitializationOfDerivedAttribute());
  }
  
  private void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new AssociationModifierCoCo());
    checker.addCoCo(new InterfaceAttributesStaticCoCo());
    checker.addCoCo(new InterfaceInvalidModifiersCoCo());
    checker.addCoCo(new AttributeNotAbstractCoCo());
    checker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
    checker.addCoCo(new ClassInvalidModifiersCoCo());
    checker.addCoCo(new EnumInvalidModifiersCoCo());
  }

  private void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new ClassModifierOnlyAbstractCoCo());
    checker.addCoCo(new ClassNoConstructorsCoCo());
    checker.addCoCo(new ClassNoMethodsCoCo());
    checker.addCoCo(new AttributeModifierOnlyDerivedCoCo());
    checker.addCoCo(new InterfaceNoModifierCoCo());
    checker.addCoCo(new InterfaceNoAttributesCoCo());
    checker.addCoCo(new InterfaceNoMethodsCoCo());
    checker.addCoCo(new EnumNoModifierCoCo());
    checker.addCoCo(new EnumNoConstructorsCoCo());
    checker.addCoCo(new EnumNoMethodsCoCo());
    checker.addCoCo(new AssociationNoStereotypesCoCo());
    checker.addCoCo(new AssociationEndModifierRestrictionCoCo());
    checker.addCoCo(new StereoValueNoValueCoCo());
  }
}
