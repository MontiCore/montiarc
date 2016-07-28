package de.monticore.automaton.ioautomaton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.types.types._ast.ASTQualifiedName;


/**
 * This is the list, containing all expressions, we view as legal by convention.
 * See also CoCo {@link UseOfForbiddenExpression}
 * 
 * @author (last commit) wortmann
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class AllowedExpressions {
  
  // List of all legal classes, put your legal Expressions here
  private static final Class<?>[] ALLOWED = {
      // TODO
//      ASTArrayAccessExpression.class,
//      ASTCastExpression.class,
//      ASTClassInstantiation.class,
//      ASTFieldAccess.class,
//      ASTInfixExpression.class,
//      ASTMethodInvocation.class,
//      ASTPostfixExpression.class,
      ASTQualifiedName.class,
//      ASTPrefixExpression.class,
//      ASTParenthesizedExpression.class
  };
  
  private Set<Class<?>> allowedExpressions = null;
  
  public AllowedExpressions() {
    this.allowedExpressions = new HashSet<Class<?>>(Arrays.asList(ALLOWED));
  }
  
  public boolean isAllowed(ASTExpression exp) {
    return allowedExpressions.contains(exp.getClass());
  }
  
}

