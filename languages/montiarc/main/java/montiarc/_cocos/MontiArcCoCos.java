/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoInputPortInEntryAction;
import arcautomaton._cocos.NoInputPortInExitAction;
import arcautomaton._cocos.NoInputPortInInitialAction;
import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
import arcautomaton._cocos.NoNonSyncInputPortInEpsilonTransition;
import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
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
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.NoPortInDefaultParameterValue;
import arcbasis._cocos.NoPortInFieldDeclaration;
import arcbasis._cocos.NoPortInSubcomponentArgument;
import arcbasis._cocos.NoPortInSuperComponentArgument;
import arcbasis._cocos.NoSubcomponentReferenceCycle;
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
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import modes._cocos.MaxOneModeAutomaton;
import modes._cocos.ModeAutomatonContainsNoStates;
import modes._cocos.ModeOmitPortDefinition;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import variablearc._cocos.ConstraintIsBoolean;
import variablearc._cocos.ConstraintNoAssignmentExpr;
import variablearc._cocos.ConstraintSatisfied4Comp;
import variablearc._cocos.ConstraintSmtConvertible;
import variablearc._cocos.ConstraintsOmitFieldReferences;
import variablearc._cocos.FeatureNameCapitalization;
import variablearc._cocos.FeatureUsage;
import variablearc._cocos.NoPortInConstraint;
import variablearc._cocos.NoPortInVarIfCondition;
import variablearc._cocos.SubcomponentsConstraint;
import variablearc._cocos.VarIfIsBoolean;
import variablearc._cocos.VarIfNoAssignmentExpr;
import variablearc._cocos.VarIfOmitFieldReferences;
import variablearc._cocos.VarIfSmtConvertible;
import variablearc._cocos.VariantAwareNoInputPortInEntryAction;
import variablearc._cocos.VariantAwareNoInputPortInExitAction;
import variablearc._cocos.VariantAwareNoInputPortInInitialAction;
import variablearc._cocos.VariantAwareNoInputPortsInInitialCompute;
import variablearc._cocos.VariantAwareNoNonSyncInputPortInCompute;
import variablearc._cocos.VariantAwareNoNonSyncInputPortInDoAction;
import variablearc._cocos.VariantAwareNoNonSyncInputPortInEpsilonTransition;
import variablearc._cocos.VariantAwareNoOtherInputPortInMsgTransition;
import variablearc._cocos.VariantAwareNoPortInDefaultParameterValue;
import variablearc._cocos.VariantAwareNoPortInFieldDeclaration;
import variablearc._cocos.VariantAwareNoPortInSubcomponentArgument;
import variablearc._cocos.VariantAwareNoPortInSuperComponentArgument;

import java.util.function.Consumer;

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
    MontiArcVariantCoCoChecker checker = new MontiArcVariantCoCoChecker();
    MontiArcCoCoChecker varChecker = checker.get4Variant();
    MontiArcTraverser traverser;  // Will be used to as intermediate memory for initializing traversers with adequate handlers etc.

    if (!checkVariants) {
      checker.addCoCo(new UnsupportedVariability());
    }

    // ArcBasis CoCos
    addCoCoAs(new CircularInheritance(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new PortsConnected(),      checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new PortUniqueSender(),    checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new SubPortsConnected(),   checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new ConnectorPortsExist(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new variablearc._cocos.arcbasis.ConnectorTypesFit(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new ConnectorDirectionsFit(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new ConnectorTimingsFit(),    checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new OnlyOneTiming());
    addCoCoAs(new AtomicNoConnector(),       checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new AtomicMaxOneBehavior(),    checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new FeedbackStrongCausality(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new OptionalConfigurationParametersLast());
    checker.addCoCo(new NoSubcomponentReferenceCycle());
    addCoCoAs(new PortHeritageTypeFits(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new FieldInitTypeFits());
    checker.addCoCo(new ParameterDefaultValueTypeFits());
    if (checkVariants) {
      varChecker.addCoCo(new VariantAwareNoPortInDefaultParameterValue());
      varChecker.addCoCo(new VariantAwareNoPortInFieldDeclaration());
      varChecker.addCoCo(new VariantAwareNoPortInSubcomponentArgument());
      varChecker.addCoCo(new VariantAwareNoPortInSuperComponentArgument());
    } else {
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
    addCoCoAs(new variablearc._cocos.arcbasis.UniqueIdentifier(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new RefinementPortsMatch());
    checker.addCoCo(new CheckNoFieldDependencyCycles());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    addCoCoAs(new BehaviorInDecomposed(), checkVariants ? varChecker::addCoCo : checker::addCoCo);

    // ArcBasis Generics CoCos
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new ComponentHeritageRawType());
    checker.addCoCo(new SubcomponentRawType());
    checker.addCoCo(new RefinementRawType());

    // VariableArc
    checker.addCoCo(new ConstraintsOmitFieldReferences());
    checker.addCoCo(new ConstraintIsBoolean());
    checker.addCoCo(new ConstraintSmtConvertible());
    checker.addCoCo(new ConstraintSatisfied4Comp());
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new SubcomponentsConstraint());
    checker.addCoCo(new VarIfOmitFieldReferences());
    checker.addCoCo(new NoPortInConstraint());
    checker.addCoCo(new NoPortInVarIfCondition());
    checker.addCoCo(new VarIfIsBoolean());
    checker.addCoCo(new VarIfSmtConvertible());

    // Modes
    checker.addCoCo(new MaxOneModeAutomaton());
    checker.addCoCo(new ModeAutomatonContainsNoStates());
    //checker.addCoCo(new ModeAutomataInDecomposedComponent());
    checker.addCoCo(new ModeOmitPortDefinition());
    checker.addCoCo(new StatechartContainsNoMode());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates(MontiArcMill.inheritanceTraverser()));
    checker.addCoCo(new TransitionSourceTargetExists());
    addCoCoAs(new TransitionPreconditionsAreBoolean(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));
    checker.addCoCo(new AnteBlockOnlyWithInitialStateModifier());

    // ArcAutomaton CoCos
    addCoCoAs(new variablearc._cocos.arcautomaton.EventTriggerExists(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));
    if (checkVariants) {
      varChecker.addCoCo(new VariantAwareNoInputPortInEntryAction());
      varChecker.addCoCo(new VariantAwareNoInputPortInExitAction());
      varChecker.addCoCo(new VariantAwareNoInputPortInInitialAction());
      varChecker.addCoCo(new VariantAwareNoNonSyncInputPortInDoAction());
      varChecker.addCoCo(new VariantAwareNoNonSyncInputPortInEpsilonTransition());
      varChecker.addCoCo(new VariantAwareNoOtherInputPortInMsgTransition());
    } else {
      checker.addCoCo(new NoInputPortInEntryAction());
      checker.addCoCo(new NoInputPortInExitAction());
      checker.addCoCo(new NoInputPortInInitialAction());
      checker.addCoCo(new NoNonSyncInputPortInDoAction());
      checker.addCoCo(new NoNonSyncInputPortInEpsilonTransition());
      checker.addCoCo(new NoOtherInputPortInMsgTransition());
    }
    // ArcAutomaton CoCos adapted for MontiArc
    addCoCoAs(new PortReadWriteInDoAction4MontiArc(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new PortReadWriteInTransition4MontiArc(), checkVariants ? varChecker::addCoCo : checker::addCoCo);

    // Unit CoCos
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // ArcCompute CoCos
    if (checkVariants) {
      varChecker.addCoCo(new VariantAwareNoInputPortsInInitialCompute());
      varChecker.addCoCo(new VariantAwareNoNonSyncInputPortInCompute());
    } else {
      checker.addCoCo(new NoInputPortsInInitialCompute());
      checker.addCoCo(new NoNonSyncInputPortInCompute());
    }
    addCoCoAs(new PortReadWriteInCompute4MontiArc(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new NoInitWithoutCompute(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new MaxOneInit(), checkVariants ? varChecker::addCoCo : checker::addCoCo);

    // MontiArc CoCos
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound());
    checker.addCoCo(new RootNoInstance());
    checker.addCoCo(new PortHeritageTimingFits());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoConnect());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());
    addCoCoAs(new ExpressionStatementIsValid(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new VarDeclarationInitializationHasCorrectType(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new ForConditionHasBooleanType(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new ForEachIsValid(),             checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new IfConditionHasBooleanType(),  checkVariants ? varChecker::addCoCo : checker::addCoCo);
    addCoCoAs(new SwitchStatementValid(),       checkVariants ? varChecker::addCoCo : checker::addCoCo);

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    return checker;
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }
}
