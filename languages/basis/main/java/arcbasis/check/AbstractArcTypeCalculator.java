/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.TypeCalculator3;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Abstract implementation of a visitor that calculates a
 * {@link SymTypeExpression} (type) for expressions in language components of
 * the MontiArc language family. Can be extended for other types of expressions
 * by adding
 * the visitors implementing {@link de.monticore.types3.AbstractTypeVisitor}.
 */
public abstract class AbstractArcTypeCalculator implements IArcTypeCalculator {

  private final TypeCalculator3 typeCalculator;

  protected AbstractArcTypeCalculator(@NotNull TypeCalculator3 tc) {
    Preconditions.checkNotNull(tc);
    this.typeCalculator = tc;
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.typeOf(node);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTExpression node, @NotNull SymTypeExpression targetType) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(targetType);
    return typeCalculator.typeOf(node, targetType);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTLiteral node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.typeOf(node);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTMCType node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.symTypeFromAST(node);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTMCReturnType node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.symTypeFromAST(node);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.symTypeFromAST(node);
  }

  @Override
  public SymTypeExpression typeOf(@NotNull ASTMCQualifiedName node) {
    Preconditions.checkNotNull(node);
    return typeCalculator.symTypeFromAST(node);
  }

  @Override
  public TypeCheckResult deriveType(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    TypeCheckResult result = new TypeCheckResult();
    result.setResult(this.typeOf(expr));
    return result;
  }

  @Override
  public TypeCheckResult deriveType(@NotNull ASTLiteral lit) {
    Preconditions.checkNotNull(lit);
    TypeCheckResult result = new TypeCheckResult();
    result.setResult(this.typeOf(lit));
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(@NotNull ASTMCType type) {
    Preconditions.checkNotNull(type);
    TypeCheckResult result = new TypeCheckResult();
    result.setResult(this.typeOf(type));
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(@NotNull ASTMCReturnType type) {
    Preconditions.checkNotNull(type);
    TypeCheckResult result = new TypeCheckResult();
    result.setResult(this.typeOf(type));
    result.setType();
    return result;
  }

  @Override
  public TypeCheckResult synthesizeType(@NotNull ASTMCQualifiedName qName) {
    Preconditions.checkNotNull(qName);
    TypeCheckResult result = new TypeCheckResult();
    result.setResult(this.typeOf(qName));
    result.setType();
    return result;
  }
}
