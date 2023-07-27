/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a set of expressions.
 * Includes expressions without modification and expressions that are in conjunction and then negated.
 * All have to hold for this set to hold.
 */
public class ExpressionSet {

  // Expressions
  protected final List<Expression> expressions;

  // Expressions where the conjunction is negated (i.e. !(e[0][0] & e[0][1]) & !(e[1][0] & ...)
  protected final List<List<Expression>> negatedConjunctions;

  public ExpressionSet() {
    this.expressions = new ArrayList<>();
    this.negatedConjunctions = new ArrayList<>();
  }

  public ExpressionSet(@NotNull List<Expression> expressions) {
    Preconditions.checkNotNull(expressions);
    this.expressions = expressions;
    this.negatedConjunctions = new ArrayList<>();
  }

  public ExpressionSet(@NotNull List<Expression> expressions, @NotNull List<List<Expression>> negatedConjunctions) {
    Preconditions.checkNotNull(expressions);
    Preconditions.checkNotNull(negatedConjunctions);
    this.expressions = expressions;
    this.negatedConjunctions = negatedConjunctions;
  }

  public ExpressionSet add(@NotNull ExpressionSet expressionSet) {
    Preconditions.checkNotNull(expressionSet);
    expressions.addAll(expressionSet.getExpressions());
    getNegatedConjunctions().addAll(expressionSet.getNegatedConjunctions());
    return this;
  }

  public List<Expression> getExpressions() {
    return expressions;
  }

  public List<List<Expression>> getNegatedConjunctions() {
    return negatedConjunctions;
  }

  public int size() {
    return expressions.size() + negatedConjunctions.size();
  }

  /**
   * @return if this expression set contains any expressions
   */
  public boolean isEmpty() {
    return expressions.isEmpty() && negatedConjunctions.isEmpty();
  }

  /**
   * Duplicates the Expression set and concatenates the prefix parameter with the existing prefix seperated by {@code "."}
   *
   * @param prefix the prefix inserted before the existing prefix
   * @return a deep copy of the original expression set with the update prefix
   */
  public ExpressionSet copyAddPrefix(@NotNull String prefix) {
    Preconditions.checkNotNull(prefix);
    return new ExpressionSet(this.getExpressions().stream().map(e -> e.copyAddPrefix(prefix)).collect(
      Collectors.toList()),
      this.getNegatedConjunctions().stream().map(l -> l.stream().map(e -> e.copyAddPrefix(prefix)).collect(
        Collectors.toList())).collect(Collectors.toList()));
  }

  /**
   * @return a deep copy of the original expression set.
   */
  public ExpressionSet copy() {
    return new ExpressionSet(this.getExpressions().stream().map(e -> e.copyWithPrefix(e.getPrefix().orElse(null))).collect(
      Collectors.toList()),
      this.getNegatedConjunctions().stream().map(l -> l.stream().map(e -> e.copyWithPrefix(e.getPrefix().orElse(null))).collect(
        Collectors.toList())).collect(Collectors.toList()));
  }

  public String print() {
    return expressions.stream().map(Expression::print).reduce((a, b) -> a + " ∧ " + b).orElse("")
      + (expressions.isEmpty() || negatedConjunctions.isEmpty() ? "" : " ∧ ") +
      negatedConjunctions.stream().map((l) -> l.stream().map(Expression::print).reduce((a, b) -> a + "∧" + b).orElse("")).reduce((a, b) -> "¬(" + a + ") ∧ ¬(" + b + ")").orElse("");
  }
}
