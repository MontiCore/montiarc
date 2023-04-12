/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * The visitor applies to assignment expressions and reports an error for each
 * assignment expression stating that the assignment expression was used in
 * an invalid context.
 */
public class OmitAssignmentExprVisitor implements AssignmentExpressionsVisitor2 {

  @Override
  public void visit(@NotNull ASTAssignmentExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcError.INVALID_CONTEXT_ASSIGNMENT.toString(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }
  @Override
  public void visit(@NotNull ASTIncPrefixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcError.INVALID_CONTEXT_INC_PREFIX.toString(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTDecPrefixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcError.INVALID_CONTEXT_DEC_PREFIX.toString(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTIncSuffixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcError.INVALID_CONTEXT_INC_SUFFIX.toString(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void visit(@NotNull ASTDecSuffixExpression node) {
    Preconditions.checkNotNull(node);
    Log.error(ArcError.INVALID_CONTEXT_DEC_SUFFIX.toString(),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }
}
