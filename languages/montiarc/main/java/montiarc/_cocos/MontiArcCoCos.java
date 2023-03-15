/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.FieldReadWriteAccessFitsInGuards;
import arcautomaton._cocos.FieldReadWriteAccessFitsInStatements;
import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.ComponentInstanceTypeExists;
import arcbasis._cocos.ComponentTypeNameCapitalization;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.ConfigurationParameterParentAssignment;
import arcbasis._cocos.ConnectorSourceAndTargetDiffer;
import arcbasis._cocos.ConnectorSourceAndTargetDirectionsFit;
import arcbasis._cocos.ConnectorSourceAndTargetExist;
import arcbasis._cocos.ConnectorSourceAndTargetTimingsFit;
import arcbasis._cocos.ConnectorSourceAndTargetTypesFit;
import arcbasis._cocos.InheritedPortsTypeCorrect;
import arcbasis._cocos.FeedbackLoopStrongCausality;
import arcbasis._cocos.FieldInitExpressionTypesCorrect;
import arcbasis._cocos.FieldInitExpressionsOmitPortReferences;
import arcbasis._cocos.FieldNameCapitalization;
import arcbasis._cocos.FieldTypeExists;
import arcbasis._cocos.InheritedComponentTypeExists;
import arcbasis._cocos.InnerComponentNotExtendsDefiningComponent;
import arcbasis._cocos.InstanceArgsOmitPortReferences;
import arcbasis._cocos.InstanceNameCapitalisation;
import arcbasis._cocos.NoBehaviorInComposedComponents;
import arcbasis._cocos.NoSubComponentReferenceCycles;
import arcbasis._cocos.OnlyOneBehavior;
import arcbasis._cocos.OnlyOneTiming;
import arcbasis._cocos.ParameterDefaultValueTypesCorrect;
import arcbasis._cocos.ParameterDefaultValuesOmitPortReferences;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.ParameterTypeExists;
import arcbasis._cocos.PortNameCapitalisation;
import arcbasis._cocos.DelayOutPortOnly;
import arcbasis._cocos.PortTypeExists;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.UniqueIdentifierNames;
import comfortablearc._cocos.MaxOneAutoconnectPerComponent;
import comfortablearc._cocos.AtomicNoAutoConnect;
import de.monticore.prettyprint.IndentPrinter;
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
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._cocos.util.CheckTypeExistence4MontiArc;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.check.MontiArcTypeCalculator;
import variablearc._cocos.*;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    MontiArcTypeCalculator maTypeCheck = new MontiArcTypeCalculator();
    TypeCalculator mcTypeCheck = new TypeCalculator(maTypeCheck, maTypeCheck);

    // ArcBasis CoCos
    checker.addCoCo(new ComponentInstanceTypeExists(new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter())));
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new ConfigurationParameterAssignment(maTypeCheck));
    //checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDirectionsFit());
    checker.addCoCo(new ConnectorSourceAndTargetExist());
    checker.addCoCo(new ConnectorSourceAndTargetTypesFit());
    checker.addCoCo(new FieldInitExpressionTypesCorrect(maTypeCheck));
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new FieldInitExpressionsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new InstanceArgsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new NoSubComponentReferenceCycles());
    checker.addCoCo(new ParameterDefaultValuesOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ParameterDefaultValueTypesCorrect(maTypeCheck));
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new ParameterTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new PortTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new PortsConnected());
    checker.addCoCo(new SubPortsConnected());
    checker.addCoCo(new UniqueIdentifierNames());

    // Inheritance CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ConfigurationParameterParentAssignment(maTypeCheck));
    checker.addCoCo(new InheritedPortsTypeCorrect());
    checker.addCoCo(new InheritedComponentTypeExists());

    // Timing CoCos
    checker.addCoCo(new ConnectorSourceAndTargetTimingsFit());
    checker.addCoCo(new FeedbackLoopStrongCausality());
    checker.addCoCo(new OnlyOneTiming());
    checker.addCoCo(new DelayOutPortOnly());

    // GenericArc CoCos
    checker.addCoCo(new GenericTypeParameterNameCapitalization());

    // VariableArc
    checker.addCoCo(new ConstraintsOmitFieldReferences());
    checker.addCoCo(new ConstraintsOmitPortReferences());
    checker.addCoCo(new ConstraintIsBoolean(new MontiArcTypeCalculator()));
    checker.addCoCo(new FeatureNameCapitalization());
    checker.addCoCo(new FeatureUsage());
    checker.addCoCo(new IfStatementsOmitFieldReferences());
    checker.addCoCo(new IfStatementsOmitPortReferences());
    checker.addCoCo(new IfStatementIsBoolean(new MontiArcTypeCalculator()));
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcBlockCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcIfStatementCoCo) new VariableElementsUsage());
    checker.addCoCo(new VariantCoCos());

    // ArcBehaviorBasis CoCos
    checker.addCoCo(new NoBehaviorInComposedComponents());
    checker.addCoCo(new OnlyOneBehavior());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates());
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new TransitionPreconditionsAreBoolean(maTypeCheck));
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    checker.addCoCo(new FieldReadWriteAccessFitsInGuards());
    checker.addCoCo(new FieldReadWriteAccessFitsInStatements());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    checker.addCoCo(new MaxOneInitialState());

    // MontiArc CoCos
    checker.addCoCo(new ComponentInheritanceRespectsGenericTypeBounds());
    checker.addCoCo(new ComponentInstantiationRespectsGenericTypeBounds());
    checker.addCoCo(new RootComponentTypesNoInstanceName());
    checker.addCoCo(new ConfigurationParameterOnlyKeywordAssignments());

    // ComfortableArc Cocos
    checker.addCoCo(new MaxOneAutoconnectPerComponent());
    checker.addCoCo(new AtomicNoAutoConnect());

    // Basic MontiCore cocos
    checker.addCoCo(new ExpressionStatementIsValid(mcTypeCheck));
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType(maTypeCheck));
    checker.addCoCo(new ForConditionHasBooleanType(mcTypeCheck));
    checker.addCoCo(new ForEachIsValid(mcTypeCheck));
    checker.addCoCo(new IfConditionHasBooleanType(mcTypeCheck));
    checker.addCoCo(new SwitchStatementValid(mcTypeCheck));

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    return checker;
  }
}
