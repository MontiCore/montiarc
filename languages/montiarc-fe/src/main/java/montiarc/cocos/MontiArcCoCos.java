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
        .addCoCo(new UniqueConstraint())
        .addCoCo(new PortNamesAreUnique())
        .addCoCo(new ComponentInstanceNamesAreUnique())
        .addCoCo(new PortUsage())
        .addCoCo(new SubComponentsConnected())
        .addCoCo(new PackageLowerCase())
        .addCoCo(new ComponentNameIsCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new ParameterNamesAreUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        
        /// Java/P Cocos ///////////////////////////////////////////////////////////// 
        .addCoCo(new SimpleConnectorSourceExists())
        .addCoCo(new ReferencedSubComponentExists())
        .addCoCo(new UsedPortsAndVariablesExist())
        .addCoCo(new MultipleBehaviorImplementation())
        /* MontiArcAutomaton Cocos */
        .addCoCo(new VariableNameIsLowerCase())
        
        /// Automaton Cocos ///////////////////////////////////////////////////////////// 
        .addCoCo(new ImplementationInNonAtomicComponent())
        .addCoCo(new AutomatonUppercase())
        
    	// CONVENTIONS
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new CorrectAssignmentOperators())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new OutputInExpression())
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo)new ReactionWithAlternatives())
        .addCoCo((MontiArcASTTransitionCoCo)new ReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo(new StateUppercase())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new DeclaredInitialStateDoesNotExist())
        .addCoCo(new UseOfUndeclaredField())
        .addCoCo(new UseOfUndeclaredState())
        .addCoCo(new AssignmentHasNoName())
        
        // TYPE CORRECTNESS
        .addCoCo(new GuardIsNotBoolean())
        .addCoCo(new StimulusTypeDoesNotFitInputType())
        .addCoCo(new InitialReactionTypeDoesNotFitOutputType())
        .addCoCo(new ReactionTypeDoesNotFitOutputType())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new StateDefinedMultipleTimesStereotypesDontMatch())
        .addCoCo(new VariableDefinedMultipleTimes())
        .addCoCo(new InitialDeclaredMultipleTimes())
        .addCoCo(new StateDefinedMultipleTimes())
        .addCoCo(new MultipleInitialStates())
        .addCoCo(new UseOfAlternatives())
        .addCoCo(new UseOfValueLists());
  }
}
