package de.montiarcautomaton.generator.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import montiarc._ast.ASTIOAssignment;
import montiarc._symboltable.AutomatonSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._symboltable.StateSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Helper class used in the template to generate target code of the automaton
 * implementation.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonHelper extends ComponentHelper {
  private final Collection<StateSymbol> states;
  private final Collection<VariableSymbol> variables;
  private final Collection<TransitionSymbol> transitions;
  
  public AutomatonHelper(AutomatonSymbol automaton, ComponentSymbol component) {
    super(component);
    
    this.states = automaton.getSpannedScope().resolveLocally(StateSymbol.KIND);
    this.transitions = automaton.getSpannedScope().resolveLocally(TransitionSymbol.KIND);
    
    this.variables = component.getEnclosingScope().resolveLocally(VariableSymbol.KIND);
  }
  
  /**
   * Returns all states of the automaton.
   * 
   * @return
   */
  public Collection<StateSymbol> getStates() {
    return states;
  }
  
  /**
   * Returns all variables not inputs or outputs of the automaton.
   * 
   * @return
   */
  public Collection<VariableSymbol> getVariables() {
    return variables;
  }
  
  /**
   * Returns the java expression of a guard.
   * 
   * @param symbol the transition
   * @return
   */
  public String getGuard(TransitionSymbol symbol) {
    if (symbol.getGuardAST().isPresent()) {
      return printExpression(symbol.getGuardAST().get().getGuardExpression().getExpression());
    }
    else {
      return null;
    }
  }
  
  /**
   * Returns a collection of io comparisons of a stimulus.
   * 
   * @param symbol the transition
   * @return
   */
  public Collection<IOAssignmentHelper> getStimulus(TransitionSymbol symbol) {
    ArrayList<IOAssignmentHelper> assignments = new ArrayList<>();
    if (symbol.getStimulusAST().isPresent()) {
      for (ASTIOAssignment assignment : symbol.getStimulusAST().get().getIOAssignments()) {
        assignments.add(new IOAssignmentHelper(assignment));
      }
    }
    return assignments;
  }
  
  /**
   * Returns a collection of io assignments of a reaction.
   * 
   * @param symbol the transition
   * @return
   */
  public Collection<IOAssignmentHelper> getReaction(TransitionSymbol symbol) {
    ArrayList<IOAssignmentHelper> assignments = new ArrayList<>();
    if (symbol.getReactionAST().isPresent()) {
      for (ASTIOAssignment assignment : symbol.getReactionAST().get().getIOAssignments()) {
        assignments.add(new IOAssignmentHelper(assignment));
      }
    }
    return assignments;
  }
  
  /**
   * Returns all transitions that start with the given source state.
   * 
   * @param state the source state
   * @return
   */
  public Collection<TransitionSymbol> getTransitions(StateSymbol state) {
    return transitions.stream().filter((symbol) -> symbol.getSource().getReferencedSymbol() == state).collect(Collectors.toList());
  }
  
  /**
   * Returns the initial state.
   * 
   * @return
   */
  public StateSymbol getInitialState() {
    List<StateSymbol> initials = states.stream().filter((state) -> state.isInitial()).collect(Collectors.toList());
    if (initials.size() != 1) {
      throw new RuntimeException("The generator only supports exactly one initial state. Current number is " + initials.size() + ".");
    }
    return initials.get(0);
  }
  
  /**
   * Returns a collection of io assignments of the initial reaction.
   * 
   * @param symbol the initial state
   * @return
   */
  public Collection<IOAssignmentHelper> getInitialReaction(StateSymbol symbol) {
    ArrayList<IOAssignmentHelper> assignments = new ArrayList<>();
    if (symbol.getInitialReactionAST().isPresent()) {
      for (ASTIOAssignment assignment : symbol.getInitialReactionAST().get().getIOAssignments()) {
        assignments.add(new IOAssignmentHelper(assignment));
      }
    }
    return assignments;
  }
  
  /**
   * Returns the java expression of the initial variable assignment.
   * 
   * @param symbol the variable
   * @return
   */
  public String getVariableInitialization(VariableSymbol symbol) {
    if (symbol.getValuation().isPresent()) {
      return printExpression(symbol.getValuation().get().getExpression());
    }
    else {
      return null;
    }
  }
  
  public String getVariableTypeName(VariableSymbol symbol) {
    return printFqnTypeName(symbol.getTypeReference());
  }
  
  /**
   * Prints an expression AST node as plain java code.
   * 
   * @param expr
   * @return
   */
  private static String printExpression(ASTExpression expr) {
    IndentPrinter printer = new IndentPrinter();
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    expr.accept(prettyPrinter);
    return printer.getContent();
  }
}
