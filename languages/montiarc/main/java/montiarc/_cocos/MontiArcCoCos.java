/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.FieldReadWriteAccessFitsInGuards;
import arcautomaton._cocos.FieldReadWriteAccessFitsInStatements;
import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.ComponentNameCapitalization;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.OptionalConfigurationParametersLast;
import arcbasis._cocos.ParameterHeritage;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.ConnectorTypesFit;
import arcbasis._cocos.DelayOutPortOnly;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.FieldInitTypeFits;
import arcbasis._cocos.FieldInitOmitPortReferences;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import arcbasis._cocos.SubcomponentNameCapitalization;
import arcbasis._cocos.NoSubcomponentReferenceCycle;
import arcbasis._cocos.OnlyOneBehavior;
import arcbasis._cocos.OnlyOneTiming;
import arcbasis._cocos.ParameterDefaultValueTypeFits;
import arcbasis._cocos.ParameterDefaultValueOmitsPortRef;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.PortNameCapitalization;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.UniqueIdentifier;
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
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeRelations;
import genericarc._cocos.ComponentHeritageTypeBound;
import genericarc._cocos.SubcomponentTypeBound;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.check.MontiArcTypeCalculator;
import variablearc._cocos.ConstraintIsBoolean;
import variablearc._cocos.ConstraintsOmitFieldReferences;
import variablearc._cocos.ConstraintsOmitPortReferences;
import variablearc._cocos.FeatureNameCapitalization;
import variablearc._cocos.FeatureUsage;
import variablearc._cocos.IfStatementIsBoolean;
import variablearc._cocos.IfStatementsOmitFieldReferences;
import variablearc._cocos.IfStatementsOmitPortReferences;
import variablearc._cocos.VariableArcASTArcBlockCoCo;
import variablearc._cocos.VariableArcASTArcIfStatementCoCo;
import variablearc._cocos.VariableElementsUsage;
import variablearc._cocos.VariantCoCos;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TypeRelations tr = new TypeRelations();
    TypeCalculator mcTypeCheck = new TypeCalculator(tc, tc, tr);

    // ArcBasis CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new PortsConnected());
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new SubPortsConnected());
    checker.addCoCo(new ConnectorPortsExist());
    checker.addCoCo(new ConnectorTypesFit(tr));
    checker.addCoCo(new ConnectorDirectionsFit());
    checker.addCoCo(new ConnectorTimingsFit());
    checker.addCoCo(new OnlyOneTiming());
    checker.addCoCo(new DelayOutPortOnly());
    checker.addCoCo(new OnlyOneBehavior());
    checker.addCoCo(new FeedbackStrongCausality());
    checker.addCoCo(new ConfigurationParameterAssignment(tc, tr));
    checker.addCoCo(new OptionalConfigurationParametersLast());
    checker.addCoCo(new NoSubcomponentReferenceCycle());
    checker.addCoCo(new ParameterHeritage(tc, tr));
    checker.addCoCo(new PortHeritageTypeFits(tr));
    checker.addCoCo(new FieldInitOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldInitTypeFits(tc, tr));
    checker.addCoCo(new ParameterDefaultValueTypeFits(tc, tr));
    checker.addCoCo(new ParameterDefaultValueOmitsPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentArgumentsOmitPortRef(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ComponentNameCapitalization());
    checker.addCoCo(new SubcomponentNameCapitalization());
    checker.addCoCo(new PortNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new UniqueIdentifier());

    // GenericArc CoCos
    checker.addCoCo(new GenericTypeParameterNameCapitalization());

    // VariableArc
    checker.addCoCo(new ConstraintsOmitFieldReferences());
    checker.addCoCo(new ConstraintsOmitPortReferences());
    checker.addCoCo(new ConstraintIsBoolean(tc, tr));
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new IfStatementsOmitFieldReferences());
    checker.addCoCo(new IfStatementsOmitPortReferences());
    checker.addCoCo(new IfStatementIsBoolean(tc, tr));
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcBlockCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcIfStatementCoCo) new VariableElementsUsage());
    checker.addCoCo(new VariantCoCos());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates());
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new TransitionPreconditionsAreBoolean(tc));
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    checker.addCoCo(new FieldReadWriteAccessFitsInGuards());
    checker.addCoCo(new FieldReadWriteAccessFitsInStatements());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    checker.addCoCo(new MaxOneInitialState());

    // MontiArc CoCos
    checker.addCoCo(new ComponentHeritageTypeBound(tr));
    checker.addCoCo(new SubcomponentTypeBound(tr));
    checker.addCoCo(new RootNoInstance());
    checker.addCoCo(new ParameterOmitAssignmentExpressions());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoConnect());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addCoCo(new ExpressionStatementIsValid(mcTypeCheck));
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType(tc));
    checker.addCoCo(new ForConditionHasBooleanType(mcTypeCheck));
    checker.addCoCo(new ForEachIsValid(mcTypeCheck));
    checker.addCoCo(new IfConditionHasBooleanType(mcTypeCheck));
    checker.addCoCo(new SwitchStatementValid(mcTypeCheck));

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    return checker;
  }
}
