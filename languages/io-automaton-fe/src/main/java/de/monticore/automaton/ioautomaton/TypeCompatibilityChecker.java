package de.monticore.automaton.ioautomaton;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Handles type conversion and resolves types of java expressions.
 * 
 * @author Gerrit Leonhardt
 */
public class TypeCompatibilityChecker {
  private static final JavaDSLHelper helper = new JavaDSLHelper();
  
  /**
   * Checks whether there exists a assignment conversion from <tt>from</tt> type
   * to <tt>target</tt> type.
   * 
   * @param from
   * @param target
   * @return
   */
  public static boolean doTypesMatch(JTypeReference<? extends JTypeSymbol> from, JTypeReference<? extends JTypeSymbol> target) {
    // io-automaton currently uses JType instead of JavaType, but type
    // helper is only implemented for JavaType, so conversion is required
    if (target instanceof JTypeReference<?> && !(target instanceof JavaTypeSymbolReference)) {
      target = new JavaTypeSymbolReference(target.getName(), target.getEnclosingScope(), target.getDimension());
    }
    if (from instanceof JTypeReference<?> && !(from instanceof JavaTypeSymbolReference)) {
      from = new JavaTypeSymbolReference(from.getName(), from.getEnclosingScope(), from.getDimension());
    }
    
    // existsAssignment conversion only implemented for JavaType not CommonJType
    // TODO Don't use JavaDSLHelper for type checking because we ant to check JTypes.
    return helper.existsAssignmentConversion((JavaTypeSymbolReference) from, (JavaTypeSymbolReference) target);
  }
  
  /**
   * Resolves the type of the given java expression. If it is not possible to
   * resolve the type, return {@link Optional#empty()}.
   * 
   * @param expr the java expression
   * @return
   */
  public static Optional<? extends JavaTypeSymbolReference> getExpressionType(ASTExpression expr) {
    // TODO Don't use HCJavaDSLTypeResolver here because we want to resolve
    // JTypes instead of JavaTypes. Because HCJavaDSLTypeResolver is implemented
    // in JavaDSL, additional adapter may required e.g. CD2Java to use CD types
    // within Java expressions.
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver(helper);
    expr.accept(typeResolver);
    return typeResolver.getResult();
  }
  
  /**
   * Checks whether there exists a assignment conversion from the expression
   * type to <tt>target</tt> type.
   * 
   * @param from
   * @param target
   * @return
   */
  public static boolean doTypesMatch(ASTExpression expr, JTypeReference<? extends JTypeSymbol> targetType) {
    Optional<? extends JavaTypeSymbolReference> exprType = getExpressionType(expr);
    if (!exprType.isPresent()) {
      Log.info("Can't resolve type of expression: " + expr, "TypeCompatibilityChecker");
      return false;
    }
    return doTypesMatch(exprType.get(), targetType);
  }
}
