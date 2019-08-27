/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import montiarc._cocos.*;
import montiarc.cocos.*;

/**
 * Context Conditions for the Dynamic MontiArc Language.
 *
 * @author (last commit) Mutert
 */
public class DynamicMontiArcCoCos {
  public static DynamicMontiArcCoCoChecker createChecker() {
    final DynamicMontiArcCoCoChecker checker =
        new DynamicMontiArcCoCoChecker();
    checker.addCoCo(new UsedTypesExist());

    checker.addCoCo(new dynamicmontiarc.cocos.PortUsage());
    checker.addCoCo(new dynamicmontiarc.cocos.SubComponentsConnected());
    checker.addCoCo(new dynamicmontiarc.cocos.InPortUniqueSender());

    checker.addCoCo(new SubcomponentParametersCorrectlyAssigned());
    checker.addCoCo(new PackageLowerCase());
    checker.addCoCo((MontiArcASTComponentCoCo)
                        new NamesCorrectlyCapitalized());
    checker.addCoCo(new DefaultParametersHaveCorrectOrder());
    checker.addCoCo(new DefaultParametersCorrectlyAssigned());
    checker.addCoCo(new ComponentWithTypeParametersHasInstance());
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new IOAssignmentCallFollowsMethodCall());
    checker.addCoCo(new AllGenericParametersOfSuperClassSet());
    checker.addCoCo(new TypeParameterNamesUnique());
    checker.addCoCo(new AmbiguousTypes());
    checker.addCoCo(new TopLevelComponentHasNoInstanceName());
    checker.addCoCo(new ConnectorEndPointIsCorrectlyQualified());
    checker.addCoCo(new ImportsValid());
    checker.addCoCo(new SubcomponentReferenceCycle());
    checker.addCoCo(new PortNamesAreNotJavaKeywords());
    checker.addCoCo(new UnusedImports());

    /// AJava Cocos
    /// /////////////////////////////////////////////////////////////
    checker.addCoCo(new AJavaInitUsedPortsAndVariablesExist());
    checker.addCoCo(new MultipleBehaviorImplementation());
    checker.addCoCo(new InitBlockOnlyOnEmbeddedAJava());
    checker.addCoCo(new AtMostOneInitBlock());
    checker.addCoCo(new AJavaUsesCorrectPortDirection());
    checker.addCoCo(new AJavaUsesExistingVariablesAndPorts());
    /* MontiArcAutomaton Cocos */

    /// Automaton Cocos
    /// /////////////////////////////////////////////////////////////
    checker.addCoCo(new ImplementationInNonAtomicComponent());

    // CONVENTIONS
    checker.addCoCo((MontiArcASTBehaviorElementCoCo)
                        new NamesCorrectlyCapitalized());
    checker.addCoCo(new AutomatonHasNoState());
    checker.addCoCo(new ArraysOfGenericTypes());
    checker.addCoCo(new AutomatonHasNoInitialState());
    checker.addCoCo(new MultipleAssignmentsSameIdentifier());
    checker.addCoCo(new AutomatonUsesCorrectPortDirection());
    checker.addCoCo((MontiArcASTInitialStateDeclarationCoCo)
                        new AutomatonReactionWithAlternatives());
    checker.addCoCo((MontiArcASTTransitionCoCo)
                        new AutomatonReactionWithAlternatives());
    checker.addCoCo((MontiArcASTIOAssignmentCoCo)
                        new UseOfForbiddenExpression());
    checker.addCoCo((MontiArcASTGuardExpressionCoCo)
                        new UseOfForbiddenExpression());
    checker.addCoCo((MontiArcASTPortCoCo) new UseOfProhibitedIdentifiers());
    checker.addCoCo((MontiArcASTVariableDeclarationCoCo)
                        new UseOfProhibitedIdentifiers());
    checker.addCoCo((MontiArcASTParameterCoCo)
                        new UseOfProhibitedIdentifiers());
    checker.addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized());
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checker.addCoCo(new ImportsAreUnique());

    // REFERENTIAL INTEGRITY
    checker.addCoCo(new UseOfUndeclaredState());
    checker.addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField());
    checker.addCoCo((MontiArcASTGuardExpressionCoCo)
                        new UseOfUndeclaredField());
    checker.addCoCo(new SubcomponentGenericTypesCorrectlyAssigned());
    checker.addCoCo(new AssignmentHasNoName());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new dynamicmontiarc.cocos.
                            UniqueTypeParamsInInnerCompHierarchy());

    // TYPE CORRECTNESS
    checker.addCoCo(new AutomatonGuardIsNotBoolean());
    checker.addCoCo(new GenericInitValues());
    checker.addCoCo(new ProhibitGenericsWithBounds());

    //checker.addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
    checker.addCoCo((MontiArcASTTransitionCoCo)
                        new AutomatonReactionTypeDoesNotFitOutputType());
    checker.addCoCo((MontiArcASTInitialStateDeclarationCoCo)
                        new AutomatonReactionTypeDoesNotFitOutputType());

    checker.addCoCo(new AutomatonNoDataAssignedToVariable());

    // UNIQUENESS OF NAMES
    checker.addCoCo(new AutomatonInitialDeclaredMultipleTimes());
    checker.addCoCo(new AutomatonStateDefinedMultipleTimes());
    checker.addCoCo(new UseOfValueLists());
    checker.addCoCo(new UsedModesAreDeclared());
    checker.addCoCo(new dynamicmontiarc.cocos.IdentifiersAreUnique());
    checker.addCoCo(new JavaPVariableIdentifiersUnique());

    // MODE AUTOMATA
    checker.addCoCo(new AtMostOneModeAutomaton());
    checker.addCoCo(new NoModeAutomatonInAtomicComponent());
    checker.addCoCo(new ModeAutomatonElementsExist());
    checker.addCoCo(new AtLeastOneTransition());
    checker.addCoCo(new ModeNameUppercase());
    checker.addCoCo(new UseUndefinedComponent());
    checker.addCoCo(new InitialMode());
    checker.addCoCo(new ConnectorUsageInModes());
    checker.addCoCo(new ModeTransitionCorrectVarAndPortUsage());
    return checker;
  }
}
