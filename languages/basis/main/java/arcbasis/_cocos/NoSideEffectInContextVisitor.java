/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import static montiarc.util.ArcError.NON_PURE_EXPRESSION;

/**
 * The visitor logs an error for non-pure expressions, i.e., any expression
 * that (potentially) has side effects. Function calls are permitted, as they
 * are assumed (but not verified) to be free of side effects.
 */
public class NoSideEffectInContextVisitor implements AssignmentExpressionsVisitor2 {

  private final String context;

  /**
   * @param context A human-readable description of the context in which this
   *                check is applied. The context is reported as part of the
   *                logged error message for non-pure expressions.
   */
  public NoSideEffectInContextVisitor(@NotNull String context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void visit(@NotNull ASTAssignmentExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(NON_PURE_EXPRESSION.format(this.context),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTIncPrefixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(NON_PURE_EXPRESSION.format(this.context),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTDecPrefixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(NON_PURE_EXPRESSION.format(this.context),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTIncSuffixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(NON_PURE_EXPRESSION.format(this.context),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTDecSuffixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(NON_PURE_EXPRESSION.format(this.context),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }
}
