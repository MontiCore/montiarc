/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import montiarc._cocos.*;

/**
 * Bundle of CoCos for the MontiArc language.
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcCoCos {
  public static MontiArcCoCoChecker createChecker() {
    return new MontiArcCoCoChecker()
        .addCoCo(new PortUsage())
        .addCoCo(new SubComponentsConnected())
        .addCoCo(new SubcomponentParametersCorrectlyAssigned())
        .addCoCo(new PackageLowerCase())
        .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new DefaultParametersCorrectlyAssigned())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new CircularInheritance())
      //TODO remove comment when new Java DSL is integrated
//        .addCoCo(new AllGenericParametersOfSuperClassSet()) 
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        .addCoCo(new ImportsValid())
        .addCoCo(new SubcomponentReferenceCycle())
        .addCoCo(new ReferencedSubComponentExists())

        /// AJava Cocos
        /// /////////////////////////////////////////////////////////////
        .addCoCo((MontiArcASTJavaPBehaviorCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new SimpleConnectorSourceExists())
        .addCoCo(new InputPortChangedInCompute())
        .addCoCo(new UsedPortsAndVariablesExist())
        .addCoCo(new MultipleBehaviorImplementation())
        .addCoCo(new InitBlockOnlyOnEmbeddedAJava())
        .addCoCo(new AtMostOneInitBlock())
        /* MontiArcAutomaton Cocos */
        
        /// Automaton Cocos
        /// /////////////////////////////////////////////////////////////
        .addCoCo(new ImplementationInNonAtomicComponent())
        
        // CONVENTIONS
        .addCoCo((MontiArcASTAutomatonBehaviorCoCo)
                     new NamesCorrectlyCapitalized())
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new CorrectAssignmentOperators())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new AutomatonOutputInExpression())
        .addCoCo(new AutomatonNoAssignmentToIncomingPort())
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo)
                     new AutomatonReactionWithAlternatives())
        .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
        .addCoCo(new ConnectorSourceAndTargetExist())
        .addCoCo(new ConnectorSourceAndTargetTypeFit())
        .addCoCo(new ImportsAreUnique())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new AutomatonDeclaredInitialStateDoesNotExist())
        .addCoCo(new UseOfUndeclaredState())
        .addCoCo(new UseOfUndeclaredField())
        .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned())
        .addCoCo(new AssignmentHasNoName())
        .addCoCo(new ConfigurationParametersCorrectlyInherited())
        .addCoCo(new InnerComponentNotExtendsDefiningComponent())
        
        // TYPE CORRECTNESS
        .addCoCo(new AutomatonGuardIsNotBoolean())
        // TODO Kann mit der Aktualisierung auf neue JavaDSL-Version aktiviert
        // werden
        // .addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
        // .addCoCo(new AutomatonInitialReactionTypeDoesNotFitOutputType())
        // .addCoCo(new AutomatonReactionTypeDoesNotFitOutputType())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new AutomatonStateDefinedMultipleTimesStereotypesDontMatch())
        .addCoCo(new AutomatonInitialDeclaredMultipleTimes())
        .addCoCo(new AutomatonStateDefinedMultipleTimes())
        .addCoCo(new UseOfValueLists())
        .addCoCo(new IdentifiersAreUnique());
  }
}
