/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.*;
import arcautomaton._cocos.*;
import arcbasis._cocos.CircularInheritance;
import arcbasis._cocos.ComponentInstanceTypeExists;
import arcbasis._cocos.ComponentTypeNameCapitalization;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._cocos.ConfigurationParametersCorrectlyInherited;
import arcbasis._cocos.ConnectorSourceAndTargetDiffer;
import arcbasis._cocos.ConnectorSourceAndTargetDirectionsFit;
import arcbasis._cocos.ConnectorSourceAndTargetExist;
import arcbasis._cocos.ConnectorSourceAndTargetTypesFit;
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
import arcbasis._cocos.ParameterDefaultValueTypesCorrect;
import arcbasis._cocos.ParameterDefaultValuesOmitPortReferences;
import arcbasis._cocos.ParameterNameCapitalization;
import arcbasis._cocos.ParameterTypeExists;
import arcbasis._cocos.PortNameCapitalisation;
import arcbasis._cocos.PortTypeExists;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortUsage;
import arcbasis._cocos.SubComponentsConnected;
import arcbasis._cocos.UniqueIdentifierNames;
import basicmodeautomata._cocos.StaticCheckOfDynamicTypes;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.sctransitions4code._cocos.AnteBlocksOnlyForInitialStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._cocos.util.CheckTypeExistence4MontiArc;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.check.MontiArcTypeCalculator;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    // ArcBasis CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ComponentInstanceTypeExists(new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter())));
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcTypeCalculator()));
    //checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDirectionsFit());
    checker.addCoCo(new ConnectorSourceAndTargetExist());
    checker.addCoCo(new ConnectorSourceAndTargetTypesFit());
    checker.addCoCo(new FieldInitExpressionTypesCorrect(new MontiArcTypeCalculator()));
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new FieldInitExpressionsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new InheritedComponentTypeExists());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new InstanceArgsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new NoSubComponentReferenceCycles());
    checker.addCoCo(new ParameterDefaultValuesOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ParameterDefaultValueTypesCorrect(new MontiArcTypeCalculator()));
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new ParameterTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new PortTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new UniqueIdentifierNames());

    // Timing CoCos
    checker.addCoCo(new ConnectorSourceAndTargetTimingsFit());
    checker.addCoCo(new FeedbackLoopTiming());
    checker.addCoCo(new OnlyOneTiming());
    checker.addCoCo(new PortTimingFits());

    // GenericArc CoCos
    checker.addCoCo(new GenericTypeParameterNameCapitalization());

    // ArcBehaviorBasis CoCos
    checker.addCoCo(new NoBehaviorInComposedComponents());
    checker.addCoCo(new OnlyOneBehavior());

    // SCBasis, SCActions, and SCTransitions4Code CoCos
    checker.addCoCo(new UniqueStates());
    checker.addCoCo(new TransitionSourceTargetExists());
    checker.addCoCo(new TransitionPreconditionsAreBoolean(new MontiArcTypeCalculator()));
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new AnteBlocksOnlyForInitialStates());

    // ArcAutomaton CoCos
    checker.addCoCo(new FieldReadWriteAccessFitsInGuards());
    checker.addCoCo(new FieldReadWriteAccessFitsInStatements());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());
    checker.addCoCo(new OneInitialStateAtMax());
    checker.addCoCo(new ExpressionStatementWellFormedness(new MontiArcTypeCalculator()));

    // MontiArc CoCos
    checker.addCoCo(new ComponentInheritanceRespectsGenericTypeBounds());
    checker.addCoCo(new ComponentInstantiationRespectsGenericTypeBounds());
    checker.addCoCo(new RootComponentTypesNoInstanceName());
    checker.addCoCo(new UnresolvableImport());

    // Basic MontiCore cocos
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType(new MontiArcTypeCalculator()));

    // Block unsupported model elements
    checker.addCoCo(new UnsupportedAutomatonElements.FinalStates());
    checker.addCoCo(new UnsupportedAutomatonElements.AutomatonStereotypes());

    // dynamic coco-checks
    checker.addCoCo(new StaticCheckOfDynamicTypes(checker.getTraverser()::getArcBasisVisitorList,
        new SubComponentsConnected(),
        new PortUsage()
    ));
    return checker;
  }
}
