/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.NoSideEffectInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;
import variablearc._visitor.VariableArcTraverser;

/**
 * The context-condition checks that varif conditions do not contain
 * expressions that (potentially) have side effects.
 */
public class VarIfSideEffectFree implements VariableArcASTArcVarIfCoCo {

  @Override
  public void check(@NotNull ASTArcVarIf node) {
    Preconditions.checkNotNull(node);

    VariableArcTraverser t = VariableArcMill.traverser();

    NoSideEffectInContextVisitor v = new NoSideEffectInContextVisitor("varif conditions");

    if (t instanceof AssignmentExpressionsTraverser) {
      ((AssignmentExpressionsTraverser) t).add4AssignmentExpressions(v);
    }

    node.getCondition().accept(t);
  }
}
