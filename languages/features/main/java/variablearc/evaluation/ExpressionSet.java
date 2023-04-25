/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

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

  // Expressions where the conjunction is negated (i.e. !(e[0][0] & e[0][1]) & (e[1][0] & ...)
  protected final List<List<Expression>> negatedConjunctions;

  public ExpressionSet(List<Expression> expressions) {
    this.expressions = expressions;
    this.negatedConjunctions = Collections.emptyList();
  }

  public ExpressionSet(List<Expression> expressions, List<List<Expression>> negatedConjunctions) {
    this.expressions = expressions;
    this.negatedConjunctions = negatedConjunctions;
  }

  public void add(ExpressionSet expressionSet) {
    expressions.addAll(expressionSet.getExpressions());
    getNegatedConjunctions().addAll(expressionSet.getNegatedConjunctions());
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

  public ExpressionSet copyWithPrefix(String prefix) {
    return new ExpressionSet(this.getExpressions().stream().map(e -> e.copyWithPrefix(prefix)).collect(
      Collectors.toList()),
      this.getNegatedConjunctions().stream().map(l -> l.stream().map(e -> e.copyWithPrefix(prefix)).collect(
        Collectors.toList())).collect(Collectors.toList()));
  }

  public ExpressionSet copyWithAddPrefix(String prefix) {
    return new ExpressionSet(this.getExpressions().stream().map(e -> e.copyAddPrefix(prefix)).collect(
      Collectors.toList()),
      this.getNegatedConjunctions().stream().map(l -> l.stream().map(e -> e.copyAddPrefix(prefix)).collect(
        Collectors.toList())).collect(Collectors.toList()));
  }
}
