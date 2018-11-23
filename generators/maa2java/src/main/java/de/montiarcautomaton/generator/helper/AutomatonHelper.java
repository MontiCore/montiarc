package de.montiarcautomaton.generator.helper;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._ast.ASTIOAssignment;
import montiarc._symboltable.ComponentSymbol;
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
  
  public AutomatonHelper(ComponentSymbol component) {
    super(component);
    
    this.states = new ArrayList<>();
    this.transitions = new ArrayList<>();
    this.variables = new ArrayList<>();
    component.getSpannedScope().getSubScopes().stream().forEach(scope -> scope
        .<StateSymbol> resolveLocally(StateSymbol.KIND).forEach(state -> this.states.add(state)));
    component.getSpannedScope().getSubScopes().stream()
        .forEach(scope -> scope.<TransitionSymbol> resolveLocally(TransitionSymbol.KIND)
            .forEach(transition -> this.transitions.add(transition)));
    // variables can only be defined in the component's body unlike in JavaP
    component.getSpannedScope().<VariableSymbol> resolveLocally(VariableSymbol.KIND)
        .forEach(variable -> this.variables.add(variable));

    
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
   * Returns a collection of io assignments of a reaction.
   * 
   * @param symbol the transition
   * @return
   */
  public Collection<IOAssignmentHelper> getReaction(TransitionSymbol symbol) {
    ArrayList<IOAssignmentHelper> assignments = new ArrayList<>();
    if (symbol.getReactionAST().isPresent()) {
      for (ASTIOAssignment assignment : symbol.getReactionAST().get().getIOAssignmentList()) {
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
    return transitions.stream()
        .filter((symbol) -> symbol.getSource().getReferencedSymbol() == state)
        .collect(Collectors.toList());
  }
  
  /**
   * Returns the initial state.
   * 
   * @return
   */
  public StateSymbol getInitialState() {
    List<StateSymbol> initials = states.stream().filter((state) -> state.isInitial())
        .collect(Collectors.toList());
    if (initials.size() != 1) {
      throw new RuntimeException(
          "The generator only supports exactly one initial state. Current number is "
              + initials.size() + ".");
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
      for (ASTIOAssignment assignment : symbol.getInitialReactionAST().get().getIOAssignmentList()) {
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
  
  public boolean isPort(String name) {
    for (VariableSymbol var : variables) {
      if (var.getName().equals(name) || var.getFullName().equals(name)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Prints an expression AST node as plain java code.
   * 
   * @param expr
   * @return
   */
  private static String printExpression(ASTExpression expr) {
    IndentPrinter printer = new IndentPrinter();
    JavaDSLPrettyPrinter prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    expr.accept(prettyPrinter);
    return printer.getContent();
  }
}
