/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * The context-condition checks that expressions of component arguments do not
 * contain assignment expressions. Component arguments are used in a
 * declarative context where side effects to arguments should be avoided
 * as they otherwise may exhibit non-deterministic behavior.
 */
public class CompArgNoAssignmentExpr implements ArcBasisASTArcArgumentCoCo {

  @Override
  public void check(@NotNull ASTArcArgument node) {
    Preconditions.checkNotNull(node);

    AssignmentExpressionsTraverser traverser = AssignmentExpressionsMill.traverser();
    traverser.add4AssignmentExpressions(new OmitAssignmentExprVisitor());
    node.getExpression().accept(traverser);
  }
}
