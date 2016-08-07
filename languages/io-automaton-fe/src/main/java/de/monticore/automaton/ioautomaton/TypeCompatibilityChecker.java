package de.monticore.automaton.ioautomaton;

import java.util.Optional;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.typeresolvers.GenericTypeResult;
import de.monticore.java.typeresolvers.JavaDSLNewHelper;
import de.monticore.java.typeresolvers.JavaDSLTypeResolver;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

public class TypeCompatibilityChecker {
  private static JavaDSLNewHelper helper = new JavaDSLNewHelper();
  
  public static boolean doTypesMatch(JTypeReference<? extends JTypeSymbol> from, JTypeReference<? extends JTypeSymbol> target) {
    // TODO io automaton currently uses JType instead of JavaType, but type helper only implemented for JavaType
    return helper.existsAssignmentConversion((JavaTypeSymbolReference) from, (JavaTypeSymbolReference) target);
  }
  
  public static Optional<? extends JavaTypeSymbolReference> getExpressionType(ASTExpression expr) {
    JavaDSLTypeResolver typeResolver = new JavaDSLTypeResolver();
    expr.accept(typeResolver);
    GenericTypeResult<JavaTypeSymbolReference> result = typeResolver.getResult();
    if (result.isIllegal()) {
      return Optional.empty();
    }
    else {
      return Optional.of(result.getValue());
    }
  }
  
  public static boolean doTypesMatch(ASTExpression expr, JTypeReference<? extends JTypeSymbol> targetType) {
    Optional<? extends JavaTypeSymbolReference> exprType = getExpressionType(expr);
    if (!exprType.isPresent()) {
      return false;
    }
    return doTypesMatch(exprType.get(), targetType);
  }
}
