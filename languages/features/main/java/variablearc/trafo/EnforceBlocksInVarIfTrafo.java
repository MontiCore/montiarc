/* (c) https://github.com/MontiCore/monticore */
package variablearc.trafo;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;
import variablearc._visitor.VariableArcVisitor2;

/**
 * Wraps an {@link ASTComponentBody} around single {@link ASTArcElement}s (that is: that are not component bodies
 * themselves), in then / else blocks of var-if statements.
 * <p>
 * Example:
 * <pre>
 *   component Comp(boolean foo) {
 *     varif (foo) Bar bar(1);
 *       else Bar bar(2);
 *
 *     component Bar(int x) { ... }
 *   }
 * </pre>
 * is transformed into:
 * <pre>
 *   component Comp(boolean foo) {
 *     varif (foo) {
 *       Bar bar(1);
 *     } else {
 *       Bar bar(2);
 *     }
 *
 *     component Bar(int x) { ... }
 *   }
 * </pre>
 */
public class EnforceBlocksInVarIfTrafo implements VariableArcVisitor2 {

  @Override
  public void visit(@NotNull ASTArcVarIf varIf) {
    Preconditions.checkNotNull(varIf);

    if (!(VariableArcMill.typeDispatcher().isASTComponentBody(varIf.getThen()))) {
      ASTArcElement wrappedThen = wrapInBlock(varIf.getThen());
      varIf.setThen(wrappedThen);
    }

    if (varIf.isPresentOtherwise() && !(VariableArcMill.typeDispatcher().isASTComponentBody(varIf.getOtherwise()))) {
      ASTArcElement wrappedOtherwise = wrapInBlock(varIf.getOtherwise());
      varIf.setOtherwise(wrappedOtherwise);
    }

  }

  protected ASTComponentBody wrapInBlock(@NotNull ASTArcElement arcEl) {
    Preconditions.checkNotNull(arcEl);

    return VariableArcMill.componentBodyBuilder()
      .addArcElement(arcEl)
      .set_SourcePositionStart(arcEl.get_SourcePositionStart())
      .set_SourcePositionEnd(arcEl.get_SourcePositionEnd())
      .build();
  }
}
