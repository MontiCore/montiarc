package de.monticore.automaton.ioautomaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;

public class ExpressionHelper {
  /**
   * Fetches the names of all Fields used in an expression
   * 
   * @param toTest The Stack of Expressions we want to test
   * @return The List of {@link ASTPrimaryExpression} with names of all used fields
   */
  public static List<ASTPrimaryExpression> collectVariables(Stack<ASTExpression> toTest) {
    List<ASTPrimaryExpression> result = new ArrayList<>();
    
    while (!toTest.empty()) {
      // Fetch element
      ASTExpression exp = toTest.pop();
      
      // primary expression
      if (exp.getPrimaryExpression().isPresent()) {
        ASTPrimaryExpression primary = exp.getPrimaryExpression().get();
        if (primary.getName().isPresent()) {
          result.add(primary);
        }
      }
      
      addSubExpressions(toTest, exp);
    }
    return result;
  }
  
  
  
  /**
   * Adds all sub expressions to the given stack.
   * 
   * @param stack
   * @param exp
   */
  public static void addSubExpressions(Stack<ASTExpression> stack, ASTExpression exp) {
    // primary expression
    if (exp.getPrimaryExpression().isPresent()) {
      ASTPrimaryExpression primary = exp.getPrimaryExpression().get();
      if (primary.getExpression().isPresent()) {
        stack.push(primary.getExpression().get());
      }
    }
    
    if (exp.getExpression().isPresent()) {
      stack.push(exp.getExpression().get());
    }
    
    // array
    if (exp.getArrayExpression().isPresent()) {
      stack.push(exp.getArrayExpression().get());
    }
    if (exp.getIndexExpression().isPresent()) {
      stack.push(exp.getIndexExpression().get());
    }
    
    // call
    if (exp.getCallExpression().isPresent()) {
      stack.push(exp.getCallExpression().get());
    }
    for (ASTExpression param : exp.getParameterExpression()) {
      stack.push(param);
    }
    
    // condition
    if (exp.getCondition().isPresent()) {
      stack.push(exp.getCondition().get());
    }
    if (exp.getTrueExpression().isPresent()) {
      stack.push(exp.getTrueExpression().get());
    }
    if (exp.getFalseExpression().isPresent()) {
      stack.push(exp.getFalseExpression().get());
    }
    
    
    if (exp.getLeftExpression().isPresent()) {
      stack.push(exp.getLeftExpression().get());
    }
    if (exp.getRightExpression().isPresent()) {
      stack.push(exp.getRightExpression().get());
    }   
  }
  
}
