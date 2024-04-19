/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoEventsInSyncAutomata;
import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
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
import arcbasis._cocos.DelayOutPortOnly;
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
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.SubcomponentNameCapitalization;
import comfortablearc._cocos.AtomicNoAutoConnect;
import comfortablearc._cocos.MaxOneAutoConnect;
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
import genericarc._cocos.ComponentHeritageRawType;
import genericarc._cocos.ComponentHeritageTypeBound;
import genericarc._cocos.SubcomponentRawType;
import genericarc._cocos.SubcomponentTypeBound;
import genericarc._cocos.TypeParameterCapitalization;
import genericarc._cocos.TypeParameterNamedTick;
import modes._cocos.MaxOneModeAutomaton;
import modes._cocos.ModeAutomataInDecomposedComponent;
import modes._cocos.ModeAutomatonContainsNoStates;
import modes._cocos.ModeOmitPortDefinition;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcMill;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
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

    // ArcBasis CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addVariantCoCo(PortsConnected.class);
    checker.addVariantCoCo(PortUniqueSender.class);
    checker.addVariantCoCo(SubPortsConnected.class);
    checker.addVariantCoCo(ConnectorPortsExist.class);
    checker.addVariantCoCo(variablearc._cocos.arcbasis.ConnectorTypesFit.class);
    checker.addVariantCoCo(ConnectorDirectionsFit.class);
    checker.addVariantCoCo(ConnectorTimingsFit.class);
    checker.addCoCo(new OnlyOneTiming());
    checker.addCoCo(new DelayOutPortOnly());
    checker.addVariantCoCo(AtomicNoConnector.class);
    checker.addVariantCoCo(AtomicMaxOneBehavior.class);
    checker.addVariantCoCo(FeedbackStrongCausality.class);
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new ConfigurationParameterAssignment(tc));
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment(tc));
    checker.addCoCo(new OptionalConfigurationParametersLast());
    checker.addCoCo(new NoSubcomponentReferenceCycle());
    checker.addVariantCoCo(PortHeritageTypeFits.class);
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
    checker.addVariantCoCo(variablearc._cocos.arcbasis.UniqueIdentifier.class);
    checker.addCoCo(new ComponentNamedTick());
    checker.addCoCo(new ComponentInstantiationNamedTick());
    checker.addCoCo(new FieldNamedTick());
    checker.addCoCo(new ParameterNamedTick());
    checker.addCoCo(new PortNamedTick());

    // GenericArc CoCos
    checker.addCoCo(new TypeParameterCapitalization());
    checker.addCoCo(new ComponentHeritageRawType());
    checker.addCoCo(new SubcomponentRawType());
    checker.addCoCo(new TypeParameterNamedTick());

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
    checker.addVariantCoCo(TransitionPreconditionsAreBoolean.class);
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));
    checker.addCoCo(new NoEventsInSyncAutomata());
    //checker.addCoCo(new NoTickEventInUntimedAutomata());

    // MontiArc CoCos
    checker.addCoCo(new ComponentHeritageTypeBound());
    checker.addCoCo(new SubcomponentTypeBound());
    checker.addCoCo(new RootNoInstance());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoConnect());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addVariantCoCo(ExpressionStatementIsValid.class);
    checker.addVariantCoCo(VarDeclarationInitializationHasCorrectType.class);
    checker.addVariantCoCo(ForConditionHasBooleanType.class);
    checker.addVariantCoCo(ForEachIsValid.class);
    checker.addVariantCoCo(IfConditionHasBooleanType.class);
    checker.addVariantCoCo(SwitchStatementValid.class);

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    return checker;
  }
}
