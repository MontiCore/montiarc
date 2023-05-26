/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.expressions;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.Optional;

/**
 * An expression consists of an ASTExpression and the information whether it should be negated.
 * The {@code prefix} is only used while converting this expression to SMT.
 */
public class Expression {

  protected final ASTExpression astExpression;
  protected final String prefix;

  public Expression(@NotNull ASTExpression astExpression) {
    this(astExpression, null);
  }

  public Expression(@NotNull ASTExpression astExpression, @Nullable String prefix) {
    Preconditions.checkNotNull(astExpression);

    this.astExpression = astExpression;
    this.prefix = prefix;
  }

  public ASTExpression getAstExpression() {
    return astExpression;
  }

  public Optional<String> getPrefix() {
    return Optional.ofNullable(prefix);
  }

  /**
   * @param prefix The new value of prefix
   * @return A new expression object with an updated prefix
   */
  public Expression copyWithPrefix(@Nullable String prefix) {
    return new Expression(astExpression, prefix);
  }

  /**
   * Duplicates the Expression and concatenates the added prefix with the existing prefix seperated by {@code "."}
   *
   * @param prefix The added prefix
   * @return A new expression object with an updated prefix
   */
  public Expression copyAddPrefix(@NotNull String prefix) {
    return copyWithPrefix(this.prefix != null ? prefix + "." + this.prefix : prefix);
  }

  public Optional<BoolExpr> convert(Context context, IDeriveSMTExpr converter) {
    converter.setPrefix(getPrefix().orElse(""));
    return converter.toBool(getAstExpression());
  }
}
