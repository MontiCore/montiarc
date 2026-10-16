/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * The context-condition checks that default values of parameters do not
 * contain expressions that (potentially) have side effects. Function calls are
 * permitted, as they are assumed (but not verified) to be free of side effects.
 */
public class ParameterDefaultValueSideEffectFree implements ArcBasisASTArcParameterCoCo {

  @Override
  public void check(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentDefault()) return;

    ArcBasisTraverser t = ArcBasisMill.traverser();

    NoSideEffectInContextVisitor v = new NoSideEffectInContextVisitor("default parameter values");

    if (t instanceof AssignmentExpressionsTraverser) {
      ((AssignmentExpressionsTraverser) t).add4AssignmentExpressions(v);
    }

    node.getDefault().accept(t);
  }
}
