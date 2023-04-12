/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.OmitAssignmentExprVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcVarIf;

/**
 * The context-condition checks that expressions in variation conditions do
 * not contain assignment expressions. Variations are used in a declarative
 * context where side effects to variables should be avoided as they otherwise
 * may exhibit non-deterministic behavior.
 */
public class VarIfNoAssignmentExpr implements VariableArcASTArcVarIfCoCo {

  @Override
  public void check(@NotNull ASTArcVarIf node) {
    Preconditions.checkNotNull(node);

    AssignmentExpressionsTraverser traverser = AssignmentExpressionsMill.traverser();
    traverser.add4AssignmentExpressions(new OmitAssignmentExprVisitor());
    node.getCondition().accept(traverser);
  }
}
