/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoEventsInSyncAutomata;
import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcautomaton._cocos.NoTickEventInUntimedAutomata;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.ConnectorTypesFit;
import arcbasis._cocos.DelayOutPortOnly;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.FieldInitOmitPortReferences;
import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.NoSubcomponentReferenceCycle;
import arcbasis._cocos.AtomicMaxOneBehavior;
import arcbasis._cocos.OnlyOneTiming;
import arcbasis._cocos.OptionalConfigurationParametersLast;
import arcbasis._cocos.ParameterDefaultValueOmitsPortRef;
import arcbasis._cocos.ParameterDefaultValueTypeFits;
import arcbasis._cocos.ParameterHeritage;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.CompArgNoAssignmentExpr;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.SubcomponentNameCapitalization;
import arcbasis._cocos.UniqueIdentifier;
import arcbasis.check.TypeCheck3AsTypeCalculator;
import comfortablearc._cocos.AtomicNoAutoConnect;
import comfortablearc._cocos.MaxOneAutoConnect;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.sctransitions4code._cocos.AnteBlocksOnlyForInitialStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import genericarc._cocos.ComponentHeritageTypeBound;
import genericarc._cocos.TypeParameterCapitalization;
import genericarc._cocos.SubcomponentTypeBound;
import genericarc._cocos.ComponentHeritageRawType;
import genericarc._cocos.SubcomponentRawType;
import modes._cocos.MaxOneModeAutomaton;
import modes._cocos.ModeAutomatonContainsNoStates;
import modes._cocos.ModeAutomataInDecomposedComponent;
import modes._cocos.ModeOmitPortDefinition;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcMill;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.check.MontiArcTypeCalculator;
import variablearc._cocos.ConstraintIsBoolean;
import variablearc._cocos.ConstraintNoAssignmentExpr;
import variablearc._cocos.ConstraintSatisfied4Comp;
import variablearc._cocos.ConstraintSmtConvertible;
import variablearc._cocos.ConstraintsOmitFieldReferences;
import variablearc._cocos.ConstraintsOmitPortReferences;
import variablearc._cocos.FeatureNameCapitalization;
import variablearc._cocos.FeatureUsage;
import variablearc._cocos.SubcomponentsConstraint;
import variablearc._cocos.VarIfIsBoolean;
import variablearc._cocos.VarIfNoAssignmentExpr;
import variablearc._cocos.VarIfOmitFieldReferences;
import variablearc._cocos.VarIfOmitPortReferences;
import variablearc._cocos.VarIfSmtConvertible;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker afterParser() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    checker.addCoCo(new CompArgNoAssignmentExpr());

    return checker;
  }

  public static MontiArcCoCoChecker afterSymTab() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TypeCheck3AsTypeCalculator tc2tc = new TypeCheck3AsTypeCalculator(tc);

    // ArcBasis CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addVariantCoCo(new PortsConnected());
    checker.addVariantCoCo(new PortUniqueSender());
    checker.addVariantCoCo(new SubPortsConnected());
    checker.addVariantCoCo(new ConnectorPortsExist());
    checker.addVariantCoCo(new ConnectorTypesFit());
    checker.addVariantCoCo(new ConnectorDirectionsFit());
    checker.addVariantCoCo(new ConnectorTimingsFit());
    checker.addCoCo(new OnlyOneTiming());
    checker.addCoCo(new DelayOutPortOnly());
    checker.addVariantCoCo(new AtomicMaxOneBehavior());
    checker.addVariantCoCo(new FeedbackStrongCausality());
    checker.addCoCo(new ConfigurationParameterAssignment(tc));
    checker.addCoCo(new OptionalConfigurationParametersLast());
    checker.addCoCo(new NoSubcomponentReferenceCycle());
    checker.addCoCo(new ParameterHeritage(tc));
    checker.addVariantCoCo(new PortHeritageTypeFits());
    checker.addCoCo(new FieldInitOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldInitTypeFits(tc));
    checker.addCoCo(new ParameterDefaultValueTypeFits(tc));
    checker.addCoCo(new ParameterDefaultValueOmitsPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentArgumentsOmitPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addVariantCoCo(new UniqueIdentifier());

    // GenericArc CoCos
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new ComponentHeritageRawType());
    checker.addCoCo(new SubcomponentRawType());

    // VariableArc
    checker.addCoCo(new ConstraintNoAssignmentExpr());
    checker.addCoCo(new ConstraintsOmitFieldReferences());
    checker.addCoCo(new ConstraintsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ConstraintIsBoolean(tc));
    checker.addCoCo(new ConstraintSmtConvertible());
    checker.addCoCo(new ConstraintSatisfied4Comp());
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new SubcomponentsConstraint());
    checker.addCoCo(new VarIfNoAssignmentExpr());
    checker.addCoCo(new VarIfOmitFieldReferences());
    checker.addCoCo(new VarIfOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new VarIfIsBoolean(tc));
    checker.addCoCo(new VarIfSmtConvertible());

    // Modes
    checker.addCoCo(new MaxOneModeAutomaton());
    checker.addCoCo(new ModeAutomatonContainsNoStates());
    checker.addCoCo(new ModeAutomataInDecomposedComponent());
    checker.addCoCo(new ModeOmitPortDefinition());
    checker.addCoCo(new StatechartContainsNoMode());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates(MontiArcMill.inheritanceTraverser()));
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new TransitionPreconditionsAreBoolean(tc));
    checker.addCoCo(new AtLeastOneInitialState(MontiArcMill.inheritanceTraverser()));
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    checker.addCoCo(new MaxOneInitialState(MontiArcMill.inheritanceTraverser()));
    checker.addCoCo(new NoEventsInSyncAutomata());
    checker.addCoCo(new NoTickEventInUntimedAutomata());

    // MontiArc CoCos
    checker.addCoCo(new ComponentHeritageTypeBound());
    checker.addCoCo(new SubcomponentTypeBound());
    checker.addCoCo(new RootNoInstance());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoConnect());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addCoCo(new ExpressionStatementIsValid(tc2tc));
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType(tc));
    checker.addCoCo(new ForConditionHasBooleanType(tc2tc));
    checker.addCoCo(new ForEachIsValid(tc2tc));
    checker.addCoCo(new IfConditionHasBooleanType(tc2tc));
    checker.addCoCo(new SwitchStatementValid(tc2tc));

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    return checker;
  }
}
