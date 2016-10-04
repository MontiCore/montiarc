package de.monticore.automata.ioautomatajava.coco;

import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTIOAssignmentCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInitialStateDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTOutputDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTTransitionCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTValuationExtCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTVariableDeclarationCoCo;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoInitialState;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoInput;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoOutput;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonHasNoState;
import de.monticore.automaton.ioautomaton.cocos.conventions.AutomatonUppercase;
import de.monticore.automaton.ioautomaton.cocos.conventions.CorrectAssignmentOperators;
import de.monticore.automaton.ioautomaton.cocos.conventions.DeclarationNamesLowerCase;
import de.monticore.automaton.ioautomaton.cocos.conventions.MultipleAssignmentsSameIdentifier;
import de.monticore.automaton.ioautomaton.cocos.conventions.ReactionWithAlternatives;
import de.monticore.automaton.ioautomaton.cocos.conventions.StateUppercase;
import de.monticore.automaton.ioautomaton.cocos.intergrity.AssignmentHasNoName;
import de.monticore.automaton.ioautomaton.cocos.intergrity.DeclaredInitialStateDoesNotExist;
import de.monticore.automaton.ioautomaton.cocos.intergrity.UseOfUndefinedState;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.InitialDeclaredMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.InputsDefinedMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.OutputsDefinedMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimes;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.StateDefinedMultipleTimesStereotypesDontMatch;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableAndIOsHaveSameName;
import de.monticore.automaton.ioautomaton.cocos.uniqueness.VariableDefinedMultipleTimes;
import de.monticore.automaton.ioautomatonjava._cocos.IOAutomatonJavaASTValuationCoCo;
import de.monticore.automaton.ioautomatonjava._cocos.IOAutomatonJavaCoCoChecker;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.OutputInExpression;
import de.monticore.automaton.ioautomatonjava.cocos.conventions.UseOfForbiddenExpression;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.GuardIsNotBoolean;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.InitialValueDoesNotFit;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.ReactionTypeDoesNotFitOutputType;
import de.monticore.automaton.ioautomatonjava.cocos.correctness.StimulusTypeDoesNotFitInputType;
import de.monticore.automaton.ioautomatonjava.cocos.integrity.UseOfUndeclaredField;
import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;

public class IOAutomatonJavaCocos {

  public static IOAutomatonJavaCoCoChecker createChecker() {
    return new IOAutomatonJavaCoCoChecker()
        // CONVENTIONS
        .addCoCo(new AutomatonHasNoState())
        .addCoCo(new AutomatonHasNoInitialState())
        .addCoCo(new CorrectAssignmentOperators())
        .addCoCo(new MultipleAssignmentsSameIdentifier())
        .addCoCo(new OutputInExpression())
        .addCoCo((IOAutomatonASTInitialStateDeclarationCoCo)new ReactionWithAlternatives())
        .addCoCo((IOAutomatonASTTransitionCoCo)new ReactionWithAlternatives())
        .addCoCo(new UseOfForbiddenExpression())
        .addCoCo(new AutomatonHasNoOutput())
        .addCoCo(new AutomatonHasNoInput())
        .addCoCo(new AutomatonUppercase())
        .addCoCo(new StateUppercase())
        .addCoCo((IOAutomatonASTInputDeclarationCoCo)new DeclarationNamesLowerCase())
        .addCoCo((IOAutomatonASTOutputDeclarationCoCo)new DeclarationNamesLowerCase())
        .addCoCo((IOAutomatonASTVariableDeclarationCoCo)new DeclarationNamesLowerCase())
        
        // REFERENTIAL INTEGRITY
        .addCoCo(new DeclaredInitialStateDoesNotExist())
        .addCoCo((IOAutomatonASTIOAssignmentCoCo)new UseOfUndeclaredField())
        .addCoCo(new UseOfUndefinedState())
        .addCoCo(new AssignmentHasNoName())
        
        // TYPE CORRECTNESS
        .addCoCo(new GuardIsNotBoolean())
        .addCoCo(new StimulusTypeDoesNotFitInputType())
        .addCoCo(new InitialReactionTypeDoesNotFitOutputType())
        .addCoCo(new ReactionTypeDoesNotFitOutputType())
        .addCoCo((IOAutomatonASTInputDeclarationCoCo)new InitialValueDoesNotFit())
        .addCoCo((IOAutomatonASTVariableDeclarationCoCo)new InitialValueDoesNotFit())
        .addCoCo((IOAutomatonASTOutputDeclarationCoCo)new InitialValueDoesNotFit())
        
        // UNIQUENESS OF NAMES
        .addCoCo(new StateDefinedMultipleTimesStereotypesDontMatch())
        .addCoCo(new OutputsDefinedMultipleTimes())
        .addCoCo(new InputsDefinedMultipleTimes())
        .addCoCo(new VariableDefinedMultipleTimes())
        .addCoCo(new VariableAndIOsHaveSameName())
        .addCoCo(new InitialDeclaredMultipleTimes())
        .addCoCo(new StateDefinedMultipleTimes());
  }
}
