/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Extracts top level {@link ASTExpression} in every AST tree you pass. I.e. top level means that the expression is not
 * part of the composition of another expression. Some limitations:
 * 1) You must register this visitor to an inheritance traverser
 * 2) Between the traversals you must manually clear the cache of this visitor by calling {@link #reset()}. Else,
 * expressions from prior traversals will be included when calling {@link #getExpressionRoots()}.
 * 3) If you start AST traversal at a non-top-level expression, then this handler can not detect this and falsely will
 * recognize that expression as being top level. Thus, this expression will be included in the results.
 */
public class ExpressionRootFinder implements ExpressionsBasisVisitor2 {

  protected final Set<ASTExpression> expressionRoots = new HashSet<>();
  protected final Deque<ASTExpression> expressionTrace = new ArrayDeque<>();

  @Override
  public void visit(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    if(expressionTrace.isEmpty()) {
      expressionRoots.add(node);
    }
    expressionTrace.push(node);
  }

  @Override
  public void endVisit(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(expressionTrace.peek().equals(node));

    expressionTrace.pop();
  }

  /**
   * Clears the cache of currently recorded top level expressions. Calling this method is a must between different AST
   * traversals. Else, expressions from prior traversals will be included when calling {@link #getExpressionRoots()}.
   */
  public void reset() {
    expressionRoots.clear();
  }

  /**
   * Get an immutable view of the currently cached top level expressions.
   */
  public ImmutableSet<ASTExpression> getExpressionRoots() {
    return ImmutableSet.copyOf(expressionRoots);
  }
}