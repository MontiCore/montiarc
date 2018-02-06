/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._cocos.MontiArcCoCoChecker;

/**
 * Bundle of CoCos for the MontiArc language.
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcCoCos {
  public static MontiArcCoCoChecker createChecker() {
    return new MontiArcCoCoChecker()
        .addCoCo(new ComponentInstanceNamesAreUnique())
        .addCoCo(new PortUsage())
        .addCoCo(new SubComponentsConnected())
        .addCoCo(new SubcomponentParametersCorrectlyAssigned())
        .addCoCo(new PackageLowerCase())
        .addCoCo(new ComponentNameIsCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new DefaultParametersCorrectlyAssigned())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        .addCoCo(new ImportsValid())
        
        /// AJava Cocos
        /// /////////////////////////////////////////////////////////////
        .addCoCo(new AJavaBehaviorNameIsUppercase())
        .addCoCo(new SimpleConnectorSourceExists())
        .addCoCo(new InputPortChangedInCompute())
        .addCoCo(new UsedPortsAndVariablesExist())
        .addCoCo(new MultipleBehaviorImplementation())
        .addCoCo(new InitBlockOnlyOnEmbeddedAJava())
        .addCoCo(new AtMostOneInitBlock())
        /* MontiArcAutomaton Cocos */
        .addCoCo(new VariableNameIsLowerCase())
        
        /// Automaton Cocos
        /// /////////////////////////////////////////////////////////////
        .addCoCo(new ImplementationInNonAtomicComponent())
        
        // CONVENTIONS
        .addCoCo(new AutomatonBehaviorNameIsUppercase())
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new CorrectAssignmentOperators())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new AutomatonOutputInExpression())
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives())
        .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo(new AutomatonStateUppercase())
        .addCoCo(new PortNameIsLowerCase())
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
        .addCoCo(new ConnectorSourceAndTargetExist())
        .addCoCo(new ImportsAreUnique())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new AutomatonDeclaredInitialStateDoesNotExist())
        .addCoCo(new UseOfUndeclaredState())
        .addCoCo(new UseOfUndeclaredField())
        .addCoCo(new AssignmentHasNoName())
        
        
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
