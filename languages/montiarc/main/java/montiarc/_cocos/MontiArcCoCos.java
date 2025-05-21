/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.AtomicMaxOneBehavior;
import arcbasis._cocos.AtomicNoConnector;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.CompArgNoAssignmentExpr;
import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import arcbasis._cocos.ComponentInstantiationNamedTick;
import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.ComponentNamedTick;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.FieldInitOmitPortReferences;
import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.FieldNamedTick;
import arcbasis._cocos.NoSubcomponentReferenceCycle;
import arcbasis._cocos.OnlyOneTiming;
import arcbasis._cocos.OptionalConfigurationParametersLast;
import arcbasis._cocos.ParameterDefaultValueOmitsPortRef;
import arcbasis._cocos.ParameterDefaultValueTypeFits;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.ParameterNamedTick;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.PortNamedTick;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.RefinementRawType;
import arcbasis._cocos.RefinementTypeBound;
import arcbasis._cocos.RefinementPortsMatch;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.SubcomponentNameCapitalization;
import comfortablearc._cocos.AtomicNoAutoConnect;
import comfortablearc._cocos.MaxOneAutoConnect;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.scstatehierarchy.NoSubstatesHandler;
import de.monticore.sctransitions4code._cocos.AnteBlocksOnlyForInitialStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import arcbasis._cocos.ComponentHeritageRawType;
import arcbasis._cocos.ComponentHeritageTypeBound;
import arcbasis._cocos.SubcomponentRawType;
import arcbasis._cocos.SubcomponentTypeBound;
import arcbasis._cocos.TypeParameterCapitalization;
import arcbasis._cocos.TypeParameterNamedTick;
import modes._cocos.MaxOneModeAutomaton;
import modes._cocos.ModeAutomatonContainsNoStates;
import modes._cocos.ModeOmitPortDefinition;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcMill;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc._visitor.MontiArcTraverser;
import variablearc._cocos.ConstraintIsBoolean;
import variablearc._cocos.ConstraintNoAssignmentExpr;
import variablearc._cocos.ConstraintSatisfied4Comp;
import variablearc._cocos.ConstraintSmtConvertible;
import variablearc._cocos.ConstraintsOmitFieldReferences;
import variablearc._cocos.ConstraintsOmitPortReferences;
import variablearc._cocos.FeatureNameCapitalization;
import variablearc._cocos.FeatureNamedTick;
import variablearc._cocos.FeatureUsage;
import variablearc._cocos.SubcomponentsConstraint;
import variablearc._cocos.VarIfIsBoolean;
import variablearc._cocos.VarIfNoAssignmentExpr;
import variablearc._cocos.VarIfOmitFieldReferences;
import variablearc._cocos.VarIfOmitPortReferences;
import variablearc._cocos.VarIfSmtConvertible;

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
    addCoCoAs(new PortHeritageTypeFits(),    checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new FieldInitOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldInitTypeFits());
    checker.addCoCo(new ParameterDefaultValueTypeFits());
    checker.addCoCo(new ParameterDefaultValueOmitsPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentArgumentsOmitPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    addCoCoAs(new variablearc._cocos.arcbasis.UniqueIdentifier(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new ComponentNamedTick());
    checker.addCoCo(new ComponentInstantiationNamedTick());
    checker.addCoCo(new FieldNamedTick());
    checker.addCoCo(new ParameterNamedTick());
    checker.addCoCo(new PortNamedTick());
    checker.addCoCo(new RefinementPortsMatch());

    // ArcBasis Generics CoCos
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new RefinementTypeBound());
    checker.addCoCo(new ComponentHeritageRawType());
    checker.addCoCo(new SubcomponentRawType());
    checker.addCoCo(new RefinementRawType());
    checker.addCoCo(new TypeParameterNamedTick());

    // VariableArc
    checker.addCoCo(new ConstraintsOmitFieldReferences());
    checker.addCoCo(new ConstraintsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ConstraintIsBoolean());
    checker.addCoCo(new ConstraintSmtConvertible());
    checker.addCoCo(new ConstraintSatisfied4Comp());
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new SubcomponentsConstraint());
    checker.addCoCo(new VarIfOmitFieldReferences());
    checker.addCoCo(new VarIfOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new VarIfIsBoolean());
    checker.addCoCo(new VarIfSmtConvertible());
    checker.addCoCo(new FeatureNamedTick());

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
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    addCoCoAs(new variablearc._cocos.arcautomaton.EventTriggerExists(), checkVariants ? varChecker::addCoCo : checker::addCoCo);
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));

    // Unit CoCos
    checker.addCoCo(new MaUnitTestConfiguredCorrectly());

    // MontiArc CoCos
    checker.addCoCo(new ComponentHeritageTypeBound());
    checker.addCoCo(new SubcomponentTypeBound());
    checker.addCoCo(new RootNoInstance());

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
