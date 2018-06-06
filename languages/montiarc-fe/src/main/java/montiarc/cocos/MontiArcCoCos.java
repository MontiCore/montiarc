/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;
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
     // TODO remove comment when new Java DSL is integrated:
        // Fails when using CD Enums due to buggy TypeCompatibilityChecker (see
        // testSubcomponentParametersOfWrongTypeWithCD in SubcomponentTest
        // class)
//        .addCoCo(new SubcomponentParametersCorrectlyAssigned())
        .addCoCo(new PackageLowerCase())
        .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new DefaultParametersCorrectlyAssigned())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new CircularInheritance())
        
        // TODO remove comment when new Java DSL is integrated
        // .addCoCo(new AllGenericParametersOfSuperClassSet())
         .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned())
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        .addCoCo(new ImportsValid())
        .addCoCo(new SubcomponentReferenceCycle())
        .addCoCo(new ReferencedSubComponentExists())
        .addCoCo(new PortNamesAreNotJavaKeywords())
        
        /// AJava Cocos
        /// /////////////////////////////////////////////////////////////
        .addCoCo((MontiArcASTJavaPBehaviorCoCo) new NamesCorrectlyCapitalized())
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
        .addCoCo((MontiArcASTAutomatonBehaviorCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new AutomatonOutputInExpression())
        .addCoCo(new AutomatonNoAssignmentToIncomingPort())
        .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives())
        .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
        .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfForbiddenExpression())
        .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression())
        .addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized())
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
        .addCoCo(new ConnectorSourceAndTargetExistAndFit())
        .addCoCo(new ImportsAreUnique())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new AutomatonDeclaredInitialStateDoesNotExist())
        .addCoCo(new UseOfUndeclaredState())
        .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField())
        .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField())
        .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned())
        // TODO see #171
        // .addCoCo(new AssignmentHasNoName())
        .addCoCo(new ConfigurationParametersCorrectlyInherited())
        .addCoCo(new InnerComponentNotExtendsDefiningComponent())
        
        // TYPE CORRECTNESS
        // TODO Kann mit der Aktualisierung auf neue JavaDSL-Version aktiviert
        // werden
        // .addCoCo(new AutomatonGuardIsNotBoolean())
        
        // .addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
        // .addCoCo(new AutomatonInitialReactionTypeDoesNotFitOutputType())
        // .addCoCo(new AutomatonReactionTypeDoesNotFitOutputType())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new AutomatonInitialDeclaredMultipleTimes())
        .addCoCo(new AutomatonStateDefinedMultipleTimes())
        .addCoCo(new UseOfValueLists())
        .addCoCo(new IdentifiersAreUnique());
  }
}
