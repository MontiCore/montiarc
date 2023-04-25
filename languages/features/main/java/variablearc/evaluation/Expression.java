/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

/**
 * An expression consists of an ASTExpression and the information whether it should be negated.
 * The {@code prefix} is only used while converting this expression to SMT.
 */
public class Expression {

  protected final ASTExpression astExpression;
  protected final boolean negated;
  protected final String prefix;

  public Expression(@NotNull ASTExpression astExpression) {
    this(astExpression, false);
  }

  public Expression(@NotNull ASTExpression astExpression, @NotNull boolean negated) {
    this(astExpression, negated, null);
  }

  public Expression(@NotNull ASTExpression astExpression, @NotNull boolean negated, @Nullable String prefix) {
    Preconditions.checkNotNull(astExpression);

    this.astExpression = astExpression;
    this.negated = negated;
    this.prefix = prefix;
  }

  public ASTExpression getAstExpression() {
    return astExpression;
  }

  public Optional<String> getPrefix() {
    return Optional.ofNullable(prefix);
  }

  public boolean isNegated() {
    return negated;
  }

  /**
   * @param prefix The new value of prefix
   * @return A new expression object with an updated prefix
   */
  public Expression copyWithPrefix(@Nullable String prefix) {
    return new Expression(astExpression, negated, prefix);
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
}
