/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbasis.check.ArcBasisSynthesizeType;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import de.monticore.types.check.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class GuardIsBoolean implements SCTransitions4CodeASTTransitionBodyCoCo {

  protected final TypeCheck typeCheck;

  public GuardIsBoolean(@NotNull TypeCheck typeCheck) {
    Preconditions.checkNotNull(typeCheck);
    this.typeCheck = typeCheck;
  }

  public GuardIsBoolean(@NotNull IDerive derive) {
    this(new TypeCheck(new ArcBasisSynthesizeType(), Preconditions.checkNotNull(derive)));
  }

  @Override
  public void check(@NotNull ASTTransitionBody transition) {
    Preconditions.checkNotNull(transition);
    if (transition.isPresentPre()) {
      SymTypeExpression guard = typeCheck.typeOf(transition.getPre());
      if (!TypeCheck.isBoolean(guard)) {
        BehaviorError.GUARD_IS_NO_BOOLEAN.logAt(transition.getPre(), guard.print());
      }
    }
  }
}