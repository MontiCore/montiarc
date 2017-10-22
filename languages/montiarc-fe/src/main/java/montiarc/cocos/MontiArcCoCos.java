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
        .addCoCo(new AutomatonOutputInExpression())
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo)new AutomatonReactionWithAlternatives())
        .addCoCo((MontiArcASTTransitionCoCo)new AutomatonReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo(new AutomatonStateUppercase())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new AutomatonDeclaredInitialStateDoesNotExist())
        .addCoCo(new UseOfUndeclaredField())
        .addCoCo(new UseOfUndeclaredState())
        .addCoCo(new AssignmentHasNoName())
        
        // TYPE CORRECTNESS
        .addCoCo(new AutomatonGuardIsNotBoolean())
        .addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
        .addCoCo(new AutomatonInitialReactionTypeDoesNotFitOutputType())
        .addCoCo(new AutomatonReactionTypeDoesNotFitOutputType())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new AutomatonStateDefinedMultipleTimesStereotypesDontMatch())
        .addCoCo(new VariableDefinedMultipleTimes())
        .addCoCo(new AutomatonInitialDeclaredMultipleTimes())
        .addCoCo(new AutomatonStateDefinedMultipleTimes())
        .addCoCo(new AutomatonMultipleInitialStates())
        .addCoCo(new UseOfAlternatives())
        .addCoCo(new UseOfValueLists());
  }
}
