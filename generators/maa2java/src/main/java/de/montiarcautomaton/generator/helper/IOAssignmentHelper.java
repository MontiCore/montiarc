package de.montiarcautomaton.generator.helper;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._ast.ASTConstantsMontiArc;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTValueList;
import montiarc._symboltable.VariableSymbol;

/**
 * Helper class used in the template to generate target code of automaton
 * behavior implementations.
 * 
 * @author Gerrit Leonhardt
 */
public class IOAssignmentHelper {
  private final ASTIOAssignment assignment;
  
  public IOAssignmentHelper(ASTIOAssignment assignment) {
    this.assignment = assignment;
  }
  
  public boolean isAssignment() {
    return assignment.isAssignment();
  }
  
  /**
   * Returns the left hand side of an assignment/comparison.
   * 
   * @return
   */
  public String getLeft() {
    return assignment.getName().get();
  }
  
  /**
   * Returns <tt>true</tt> if the given name is a variable name.
   * 
   * @param name
   * @return
   */
  public boolean isVariable(String name) {
    Optional<VariableSymbol> symbol = assignment.getEnclosingScope().get().<VariableSymbol> resolve(name, VariableSymbol.KIND);
    if (symbol.isPresent()) {
      return true;
    }
    return false;
  }
  
  /**
   * Returns the right side of an assignment/comparison. ValueLists &
   * Alternatives are not supported.
   * 
   * @return
   */
  public String getRight() {
    if (assignment.alternativeIsPresent()) {
      throw new RuntimeException("Alternatives not supported.");
    }
    else {
      final ASTValueList vl = assignment.getValueList().get();
      if (vl.valuationIsPresent()) {
        return printExpression(vl.getValuation().get().getExpression());
      }
      else {
        throw new RuntimeException("ValueLists not supported.");
      }
    }
  }
  
  
  /**
   * Prints the java expression of the given AST node.
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
