package de.monticore.automaton.ioautomaton.cocos;

import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInitialStateDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTTransitionCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTVariableDeclarationCoCo;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoInitialState;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoState;
import de.monticore.automaton.ioautomaton.cocos.conventions.CorrectAssignmentOperators;
import de.monticore.automaton.ioautomaton.cocos.conventions.DeclarationNamesLowerCase;
import de.monticore.automaton.ioautomaton.cocos.conventions.MultipleAssignmentsSameIdentifier;
import de.monticore.automaton.ioautomaton.cocos.conventions.ReactionWithAlternatives;
import de.monticore.automaton.ioautomaton.cocos.conventions.StateUppercase;
import de.monticore.automaton.ioautomaton.cocos.intergrity.AssignmentHasNoName;
import de.monticore.automaton.ioautomaton.cocos.intergrity.DeclaredInitialStateDoesNotExist;
import de.monticore.automaton.ioautomaton.cocos.intergrity.MultipleInitialStates;
import de.monticore.automaton.ioautomaton.cocos.intergrity.UseOfAlternatives;
import de.monticore.automaton.ioautomaton.cocos.intergrity.UseOfUndeclaredState;
import de.monticore.automaton.ioautomaton.cocos.intergrity.UseOfValueLists;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.InitialDeclaredMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimesStereotypesDontMatch;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableAndIOsHaveSameName;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableDefinedMultipleTimes;
import de.monticore.automaton.ioautomatonjava._cocos.IOAutomatonJavaCoCoChecker;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.OutputInExpression;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.UseOfForbiddenExpression;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.GuardIsNotBoolean;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialValueDoesNotFit;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.ReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.StimulusTypeDoesNotFitInputType;
import de.monticore.automaton.ioautomatonjava.cocos.integrity.UseOfUndeclaredField;

/**
 * CoCos for IOAutomatonJava language.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class IOAutomatonJavaCoCos {
  public static IOAutomatonJavaCoCoChecker createChecker() {
    IOAutomatonJavaCoCoChecker checker = new IOAutomatonJavaCoCoChecker();
    return checker
        // CONVENTIONS
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new CorrectAssignmentOperators())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new OutputInExpression())
        .addCoCo((IOAutomatonASTInitialStateDeclarationCoCo)new ReactionWithAlternatives())
        .addCoCo((IOAutomatonASTTransitionCoCo)new ReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo(new StateUppercase())
        .addCoCo((IOAutomatonASTVariableDeclarationCoCo)new DeclarationNamesLowerCase())
        
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
        .addCoCo((IOAutomatonASTVariableDeclarationCoCo)new InitialValueDoesNotFit())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new StateDefinedMultipleTimesStereotypesDontMatch())
        .addCoCo(new VariableDefinedMultipleTimes())
        .addCoCo(new VariableAndIOsHaveSameName())
        .addCoCo(new InitialDeclaredMultipleTimes())
        .addCoCo(new StateDefinedMultipleTimes())
        .addCoCo(new MultipleInitialStates())
        .addCoCo(new UseOfAlternatives())
        .addCoCo(new UseOfValueLists())
        ;
  }
  
  
}
