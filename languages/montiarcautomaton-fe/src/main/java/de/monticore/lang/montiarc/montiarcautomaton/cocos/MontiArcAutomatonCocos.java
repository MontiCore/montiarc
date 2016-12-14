package de.monticore.lang.montiarc.montiarcautomaton.cocos;

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
import de.monticore.automaton.ioautomaton.cocos.intergrity.UseOfUndeclaredState;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.InitialDeclaredMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimesStereotypesDontMatch;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableAndIOsHaveSameName;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableDefinedMultipleTimes;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.OutputInExpression;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.UseOfForbiddenExpression;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.GuardIsNotBoolean;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialValueDoesNotFit;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.ReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.StimulusTypeDoesNotFitInputType;
import de.monticore.automaton.ioautomatonjava.cocos.integrity.UseOfUndeclaredField;
import de.monticore.lang.montiarc.cocos.MontiArcCoCos;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonHasInput;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonHasOutput;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonUppercase;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.MultipleBehaviorImplementation;

public class MontiArcAutomatonCocos {
  public static MontiArcAutomatonCoCoChecker createChecker() {
    MontiArcAutomatonCoCoChecker checker = new MontiArcAutomatonCoCoChecker();
    checker.addChecker(MontiArcCoCos.createChecker());
    
    // add all required IO-Automaton cocos
    createIOAutomatonJavaChecker(checker);
    
    // add all MontiArcAutomaton specific cocos
    return checker
        .addCoCo(new AutomatonUppercase()) // reimplement AutomatonUppercase
        .addCoCo(new AutomatonHasInput()) // inputs forbidden
        .addCoCo(new AutomatonHasOutput()) // outputs forbidden
        .addCoCo(new MultipleBehaviorImplementation())
        ;
  }
  
  /**
   * Adds all IO-Automaton cocos used for MontiArcAutomaton.
   * @return
   */
  private static MontiArcAutomatonCoCoChecker createIOAutomatonJavaChecker(MontiArcAutomatonCoCoChecker checker) {
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
//    .addCoCo(new AutomatonHasNoOutput()) not in use for maa
//    .addCoCo(new AutomatonHasNoInput()) not in use for maa
//    .addCoCo(new AutomatonUppercase()) not in use for maa
    .addCoCo(new StateUppercase())
//    .addCoCo((IOAutomatonASTInputDeclarationCoCo)new DeclarationNamesLowerCase()) not in use for maa
//    .addCoCo((IOAutomatonASTOutputDeclarationCoCo)new DeclarationNamesLowerCase()) not in use for maa
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
//    .addCoCo((IOAutomatonASTInputDeclarationCoCo)new InitialValueDoesNotFit()) not in use for maa
    .addCoCo((IOAutomatonASTVariableDeclarationCoCo)new InitialValueDoesNotFit())
//    .addCoCo((IOAutomatonASTOutputDeclarationCoCo)new InitialValueDoesNotFit()) not in use for maa
    
    // UNIQUENESS OF NAMES
    .addCoCo(new StateDefinedMultipleTimesStereotypesDontMatch())
//    .addCoCo(new OutputsDefinedMultipleTimes()) not in use for maa
//    .addCoCo(new InputsDefinedMultipleTimes()) not in use for maa
    .addCoCo(new VariableDefinedMultipleTimes())
    .addCoCo(new VariableAndIOsHaveSameName())
    .addCoCo(new InitialDeclaredMultipleTimes())
    .addCoCo(new StateDefinedMultipleTimes())
    ;
  }
}
