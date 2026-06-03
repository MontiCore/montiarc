/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.EventTriggerExists;
import arcautomaton._cocos.NoInputPortInEntryAction;
import arcautomaton._cocos.NoInputPortInExitAction;
import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
import arcautomaton._cocos.NoNonSyncInputPortInEpsilonTransition;
import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
import arcautomaton._cocos.NoStatechartAnteAction;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.AtomicMaxOneBehavior;
import arcbasis._cocos.AtomicNoConnector;
import arcbasis._cocos.BehaviorInDecomposed;
import arcbasis._cocos.CheckNoFieldDependencyCycles;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.CompArgNoAssignmentExpr;
import arcbasis._cocos.ComponentHeritageRawType;
import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.ConnectorTypesFit;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.NoComponentReferenceCycle;
import arcbasis._cocos.NoFieldInDefaultParameterValue;
import arcbasis._cocos.NoFieldInSuperComponentArgument;
import arcbasis._cocos.NoFieldInSubcomponentArgument;
import arcbasis._cocos.NoPortInDefaultParameterValue;
import arcbasis._cocos.NoPortInFieldDeclaration;
import arcbasis._cocos.NoPortInSubcomponentArgument;
import arcbasis._cocos.NoPortInSuperComponentArgument;
import arcbasis._cocos.OnlyAssignmentOrCallExpressionStatement;
import arcbasis._cocos.OnlyOneTiming;
import arcbasis._cocos.OptionalConfigurationParametersLast;
import arcbasis._cocos.ParameterDefaultValueTypeFits;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortHeritageTimingFits;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.RefinementPortsMatch;
import arcbasis._cocos.RefinementRawType;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.SubcomponentNameCapitalization;
import arcbasis._cocos.SubcomponentRawType;
import arcbasis._cocos.TypeBound;
import arcbasis._cocos.TypeParameterCapitalization;
import arccompute._cocos.MaxOneInit;
import arccompute._cocos.NoInitWithoutCompute;
import arccompute._cocos.NoInputPortsInInitialCompute;
import arccompute._cocos.NoNonSyncInputPortInCompute;
import comfortablearc._cocos.AtomicNoAutoConnect;
import comfortablearc._cocos.MaxOneAutoConnect;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.monticore.scbasis._cocos.AnteBlockOnlyWithInitialStateModifier;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.scstatehierarchy.NoSubstatesHandler;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchCaseTypesValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import modes._cocos.MaxOneModeAutomaton;
import modes._cocos.ModeAutomatonContainsNoStates;
import modes._cocos.ModeOmitPortDefinition;
import modes._cocos.NoCodeBlockInModeTransitions;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import variablearc._cocos.AtomicMaxOneBehavior4Family;
import variablearc._cocos.AtomicNoConnector4Family;
import variablearc._cocos.CircularInheritance4Family;
import variablearc._cocos.ConnectorDirectionsFit4Family;
import variablearc._cocos.ConnectorPortsExist4Family;
import variablearc._cocos.ConnectorTimingsFit4Family;
import variablearc._cocos.ConstraintIsBoolean;
import variablearc._cocos.ConstraintNoAssignmentExpr;
import variablearc._cocos.ConstraintSatisfied4Comp;
import variablearc._cocos.ConstraintSmtConvertible;
import variablearc._cocos.NoFieldInConstraint;
import variablearc._cocos.FeatureNameCapitalization;
import variablearc._cocos.FeatureUsage;
import variablearc._cocos.FeedbackStrongCausality4Family;
import variablearc._cocos.NoFieldInVarIfCondition;
import variablearc._cocos.NoPortInConstraint;
import variablearc._cocos.NoPortInVarIfCondition;
import variablearc._cocos.PortUniqueSender4Family;
import variablearc._cocos.PortsConnected4Family;
import variablearc._cocos.SubPortsConnected4Family;
import variablearc._cocos.SubcomponentsConstraint;
import variablearc._cocos.UniqueIdentifier4Family;
import variablearc._cocos.VarIfIsBoolean;
import variablearc._cocos.VarIfNoAssignmentExpr;

import variablearc._cocos.VarIfSmtConvertible;
import variablearc._cocos.arcbasis.UniqueIdentifier;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker afterParser() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    checker.addCoCo(new CompArgNoAssignmentExpr());
    checker.addCoCo(new ConstraintNoAssignmentExpr());
    checker.addCoCo(new VarIfNoAssignmentExpr());

    return checker;
  }

  public static MontiArcCoCoChecker afterSymTab1() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());
    return checker;
  }

  public static MontiArcCoCoChecker afterSymTab2() {
    return afterSymTab2(true);
  }

  public static MontiArcCoCoChecker afterSymTab2(boolean checkVariants) {
    MontiArcFullVariantCoCoChecker varChecker = new MontiArcFullVariantCoCoChecker();
    MontiArcCoCoChecker checker = checkVariants ? varChecker : new MontiArcCoCoChecker();
    MontiArcTraverser traverser;

    if (!checkVariants) {
      checker.addCoCo(new UnsupportedVariability());
    }

    // ArcBasis CoCos
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new CircularInheritance4Family());
      varChecker.get4FullVariant().addCoCo(new PortsConnected4Family());
      varChecker.get4FullVariant().addCoCo(new PortUniqueSender4Family());
      varChecker.get4FullVariant().addCoCo(new SubPortsConnected4Family());
      varChecker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());
      varChecker.get4FullVariant().addCoCo(new ConnectorTypesFit4Family());
      varChecker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());
      varChecker.get4FullVariant().addCoCo(new ConnectorTimingsFit4Family());
      varChecker.get4FullVariant().addCoCo(new AtomicNoConnector4Family());
      varChecker.get4FullVariant().addCoCo(new AtomicMaxOneBehavior4Family());
      varChecker.get4FullVariant().addCoCo(new FeedbackStrongCausality4Family());
    } else {
      checker.addCoCo(new CircularInheritance());
      checker.addCoCo(new PortsConnected());
      checker.addCoCo(new PortUniqueSender());
      checker.addCoCo(new SubPortsConnected());
      checker.addCoCo(new ConnectorPortsExist());
      checker.addCoCo(new ConnectorTypesFit());
      checker.addCoCo(new ConnectorDirectionsFit());
      checker.addCoCo(new ConnectorTimingsFit());
      checker.addCoCo(new OnlyOneTiming());
      checker.addCoCo(new AtomicNoConnector());
      checker.addCoCo(new AtomicMaxOneBehavior());
      checker.addCoCo(new FeedbackStrongCausality());
    }
    checker.addCoCo(new OptionalConfigurationParametersLast());
    checker.addCoCo(new NoComponentReferenceCycle());
    checker.addCoCo(new PortHeritageTimingFits());
    checker.addCoCo(new PortHeritageTypeFits());
    checker.addCoCo(new FieldInitTypeFits());
    checker.addCoCo(new ParameterDefaultValueTypeFits());
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new NoPortInDefaultParameterValue4Family());
      varChecker.get4FullVariant().addCoCo(new NoPortInFieldDeclaration4Family());
      varChecker.get4FullVariant().addCoCo(new NoPortInSubcomponentArgument4Family());
      varChecker.get4FullVariant().addCoCo(new NoPortInSuperComponentArgument4Family());
    } else {
      checker.addCoCo(new NoFieldInSuperComponentArgument());
      checker.addCoCo(new NoFieldInDefaultParameterValue());
      checker.addCoCo(new NoFieldInSubcomponentArgument());
      checker.addCoCo(new NoPortInDefaultParameterValue());
      checker.addCoCo(new NoPortInFieldDeclaration());
      checker.addCoCo(new NoPortInSubcomponentArgument());
      checker.addCoCo(new NoPortInSuperComponentArgument());
    }
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new UniqueIdentifier4Family());
    } else {
      checker.addCoCo(new UniqueIdentifier());
    }
    checker.addCoCo(new RefinementPortsMatch());
    checker.addCoCo(new CheckNoFieldDependencyCycles());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    if (checkVariants) {
      //varChecker.get4FullVariant().addCoCo(new BehaviorInDecomposed4Family());
    } else {
      checker.addCoCo(new BehaviorInDecomposed());
    }
    checker.addCoCo(new OnlyAssignmentOrCallExpressionStatement());

    // ArcBasis Generics CoCos
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new ComponentHeritageRawType());
    checker.addCoCo(new SubcomponentRawType());
    checker.addCoCo(new RefinementRawType());

    // VariableArc
    checker.addCoCo(new NoFieldInConstraint());
    checker.addCoCo(new ConstraintIsBoolean());
    checker.addCoCo(new ConstraintSmtConvertible());
    checker.addCoCo(new ConstraintSatisfied4Comp());
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new SubcomponentsConstraint());
    checker.addCoCo(new NoPortInConstraint());
    if (checkVariants) {
      checker.addCoCo(new NoFieldInVarIfCondition());
      checker.addCoCo(new NoPortInVarIfCondition());
      checker.addCoCo(new VarIfIsBoolean());
      checker.addCoCo(new VarIfSmtConvertible());
    }

    // Modes
    checker.addCoCo(new MaxOneModeAutomaton());
    checker.addCoCo(new ModeAutomatonContainsNoStates());
    checker.addCoCo(new NoCodeBlockInModeTransitions());
    //checker.addCoCo(new ModeAutomataInDecomposedComponent());
    checker.addCoCo(new ModeOmitPortDefinition());
    checker.addCoCo(new StatechartContainsNoMode());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates(MontiArcMill.inheritanceTraverser()));
    checker.addCoCo(new TransitionSourceTargetExists());
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());
    } else {
      checker.addCoCo(new TransitionPreconditionsAreBoolean());
    }
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));
    checker.addCoCo(new AnteBlockOnlyWithInitialStateModifier());

    // ArcAutomaton CoCos
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new EventTriggerExists4Family());
      varChecker.get4FullVariant().addCoCo(new NoStatechartAnteAction());
    } else {
      checker.addCoCo(new EventTriggerExists());
      checker.addCoCo(new NoStatechartAnteAction());
    }
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new NoInputPortInEntryAction4Family());
      varChecker.get4FullVariant().addCoCo(new NoInputPortInExitAction4Family());
      varChecker.get4FullVariant().addCoCo(new NoNonSyncInputPortInDoAction4Family());
      varChecker.get4FullVariant().addCoCo(new NoNonSyncInputPortInEpsilonTransition4Family());
      varChecker.get4FullVariant().addCoCo(new NoOtherInputPortInMsgTransition4Family());
      varChecker.get4FullVariant().addCoCo(new PortReadWriteInDoAction4Family());
      //varChecker.get4FullVariant().addCoCo(new PortReadWriteInTransition4MontiArc4Family());
    } else {
      checker.addCoCo(new NoInputPortInEntryAction());
      checker.addCoCo(new NoInputPortInExitAction());
      checker.addCoCo(new NoNonSyncInputPortInDoAction());
      checker.addCoCo(new NoNonSyncInputPortInEpsilonTransition());
      checker.addCoCo(new NoOtherInputPortInMsgTransition());
      checker.addCoCo(new PortReadWriteInDoAction4MontiArc());
      checker.addCoCo(new PortReadWriteInTransition4MontiArc());
    }
    // ArcAutomaton CoCos adapted for MontiArc

    // Unit CoCos
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // ArcCompute CoCos
    if (checkVariants) {
      varChecker.get4FullVariant().addCoCo(new NoInputPortsInInitialCompute4Family());
      varChecker.get4FullVariant().addCoCo(new NoNonSyncInputPortInCompute4Family());
      varChecker.get4FullVariant().addCoCo(new PortReadWriteInCompute4Family());
      //varChecker.get4FullVariant().addCoCo(new NoInitWithoutCompute4Family());
      //varChecker.get4FullVariant().addCoCo(new MaxOneInit4Family());
    } else {
      checker.addCoCo(new NoInputPortsInInitialCompute());
      checker.addCoCo(new NoNonSyncInputPortInCompute());
      checker.addCoCo(new PortReadWriteInCompute4MontiArc());
      checker.addCoCo(new NoInitWithoutCompute());
      checker.addCoCo(new MaxOneInit());
    }

    // MontiArc CoCos
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());
    checker.addCoCo(new RootNoInstance());
    checker.addCoCo(new ImportedSymbolExists());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoConnect());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());
    if (checkVariants) {
        varChecker.get4FullVariant().addCoCo(new ExpressionStatementIsValid4Family());
        varChecker.get4FullVariant().addCoCo(new VarDeclarationInitializationHasCorrectType4Family());
        varChecker.get4FullVariant().addCoCo(new ForConditionHasBooleanType4Family());
        varChecker.get4FullVariant().addCoCo(new ForEachIsValid4Family());
        varChecker.get4FullVariant().addCoCo(new IfConditionHasBooleanType4Family());
        //varChecker.get4FullVariant().addCoCo(new SwitchCaseCompatible4Family());
    } else {
      checker.addCoCo(new ExpressionStatementIsValid());
      checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
      checker.addCoCo(new ForConditionHasBooleanType());
      checker.addCoCo(new ForEachIsValid4MA());
      checker.addCoCo(new IfConditionHasBooleanType());
      checker.addCoCo(new SwitchCaseTypesValid());
    }

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());

    return checkVariants? varChecker : checker;
  }
}
