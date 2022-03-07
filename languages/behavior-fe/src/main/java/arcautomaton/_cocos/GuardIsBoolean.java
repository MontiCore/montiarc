/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbasis.check.ArcBasisDeriveType;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class GuardIsBoolean implements SCTransitions4CodeASTTransitionBodyCoCo {

  /**
   * Used to extract the type of the guard expression. We use the IDerive to prevent exceptions thrown by MontiCore in
   * case the expression is malformed.
   */
  protected final IDerive typeDeriver;

  /**
   * Creates this coco with an {@link ArcBasisDeriveType}.
   *
   * @see #GuardIsBoolean(IDerive)
   */
  public GuardIsBoolean() {
    this(new ArcBasisDeriveType());
  }

  /**
   * Creates this coco with a custom {@link IDerive} to extract the type of the guard expression.
   *
   * @see #typeDeriver
   */
  public GuardIsBoolean(@NotNull IDerive typeDeriver) {
    this.typeDeriver = Preconditions.checkNotNull(typeDeriver);
  }

  /**
   * Checks to which type the {@code expression} evaluates to and returns it, wrapped in an optional. If the expression
   * does not evaluate to a type, e.g., because it is malformed, the returned optional is empty.
   */
  protected Optional<SymTypeExpression> extractTypeOf(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);

    this.typeDeriver.init();
    expression.accept(this.typeDeriver.getTraverser());
    return this.typeDeriver.getResult();
  }


  @Override
  public void check(@NotNull ASTTransitionBody transition) {
    Preconditions.checkNotNull(transition);
    if (transition.isPresentPre()) {
      Optional<SymTypeExpression> guard = this.extractTypeOf(transition.getPre());
      if (guard.isPresent() && !TypeCheck.isBoolean(guard.get())) {
        BehaviorError.GUARD_IS_NO_BOOLEAN.logAt(transition.getPre(), guard.get().print());
      }
      if (!guard.isPresent()) {
        Log.debug(String.format("Checking coco '%s' is skipped for the guard expression, as the its type could not " +
              "be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), transition.getPre().get_SourcePositionStart()),
          "CoCos");
      }
    }
  }
}