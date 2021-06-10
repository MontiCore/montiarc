/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import arcautomaton.ArcAutomatonMill;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public class ExpressionRootFinder implements ExpressionsBasisVisitor2 {
  /**
   * Saves whether the last visited node was an expression or not.
   * So if {@link Deque#peek()} changes from <code>null</code> or <code>false</code> to <code>true</code>,
   * the root of an expression was found
   */
  protected final Deque<Boolean> isInExpression;

  /**
   * Action that should be applied to every found root of an expression-tree
   */
  protected final Consumer<ASTExpression> bucket;

  /**
   * creates a fresh new object that will identify the first expressions it finds as expression-root
   * @param bucket action to apply to found expressions
   */
  public ExpressionRootFinder(@NotNull Consumer<ASTExpression> bucket) {
    Preconditions.checkNotNull(bucket);
    this.bucket = bucket;
    this.isInExpression = new LinkedList<>();
  }

  /**
   * creates a traverser and marries it to this visitor
   * @return a traverser that contains this visitor
   */
  public ArcAutomatonTraverser createTraverser() {
    ArcAutomatonTraverser traverser = ArcAutomatonMill.inheritanceTraverser();
    traverser.add4IVisitor(this);
    traverser.add4ExpressionsBasis(this);
    return traverser;
  }

  @Override
  public void visit(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    if(isInExpression.peek() != Boolean.TRUE){
      bucket.accept(node);
    }
    isInExpression.push(true);
  }

  @Override
  public void visit(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    if(node instanceof ASTExpression) {
      return;
    }
    isInExpression.push(false);
  }

  @Override
  public void endVisit(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    isInExpression.pop();
  }
}