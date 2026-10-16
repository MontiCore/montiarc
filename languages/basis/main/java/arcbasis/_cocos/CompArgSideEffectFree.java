/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * The context-condition checks that component arguments do not contain
 * expressions that (potentially) have side effects. Function calls are
 * permitted, as they are assumed (but not verified) to be free of side effects.
 */
public class CompArgSideEffectFree implements ArcBasisASTArcArgumentCoCo {

  @Override
  public void check(@NotNull ASTArcArgument node) {
    Preconditions.checkNotNull(node);

    ArcBasisTraverser t = ArcBasisMill.traverser();

    NoSideEffectInContextVisitor v = new NoSideEffectInContextVisitor("component arguments");

    if (t instanceof AssignmentExpressionsTraverser) {
      ((AssignmentExpressionsTraverser) t).add4AssignmentExpressions(v);
    }

    node.getExpression().accept(t);
  }
}
