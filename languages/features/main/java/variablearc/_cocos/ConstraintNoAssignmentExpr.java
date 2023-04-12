/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.OmitAssignmentExprVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * The context-condition checks that expressions in constraints do not contain
 * assignment expressions. Constraints are used in a declarative context where
 * side effects to variables should be avoided as they otherwise may exhibit
 * non-deterministic behavior.
 */
public class ConstraintNoAssignmentExpr implements VariableArcASTArcConstraintDeclarationCoCo {

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);

    AssignmentExpressionsTraverser traverser = AssignmentExpressionsMill.traverser();
    traverser.add4AssignmentExpressions(new OmitAssignmentExprVisitor());
    node.getExpression().accept(traverser);
  }
}
