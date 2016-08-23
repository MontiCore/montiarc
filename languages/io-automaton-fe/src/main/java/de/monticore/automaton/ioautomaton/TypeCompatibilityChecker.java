package de.monticore.automaton.ioautomaton;

import java.nio.file.ClosedDirectoryStreamException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.ASTExpressionHelper;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.CommonJTypeScope;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

public class TypeCompatibilityChecker {
  private static JavaDSLHelper helper = new JavaDSLHelper();
  
  public static boolean doTypesMatch(JTypeReference<? extends JTypeSymbol> from, JTypeReference<? extends JTypeSymbol> target) {
    // TODO io automaton currently uses JType instead of JavaType, but type helper only implemented for JavaType
    if (target instanceof CommonJTypeReference<?>) { // existsAssignment conversion only implemented for JavaType not CommonJType
      target = new JavaTypeSymbolReference(target.getName(), target.getEnclosingScope(), target.getDimension());
    }
    return helper.existsAssignmentConversion((JavaTypeSymbolReference) from, (JavaTypeSymbolReference) target);
  }
  
  public static Optional<? extends JavaTypeSymbolReference> getExpressionType(ASTExpression expr) {
    HCJavaDSLTypeResolver typeResolver = new HCJavaDSLTypeResolver(helper);
    expr.accept(typeResolver);
    return typeResolver.getResult();
  }
  
  public static boolean doTypesMatch(ASTExpression expr, JTypeReference<? extends JTypeSymbol> targetType) {
    Optional<? extends JavaTypeSymbolReference> exprType = getExpressionType(expr);
    if (!exprType.isPresent()) {
      Log.info("can't resolve type of expression: " + expr, "TypeCompatibilityChecker");
      return false;
    }
    return doTypesMatch(exprType.get(), targetType);
  }
}
