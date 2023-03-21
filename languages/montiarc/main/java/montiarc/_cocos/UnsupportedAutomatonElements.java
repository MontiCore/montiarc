/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._cocos.SCActionsASTSCEntryActionCoCo;
import de.monticore.scactions._cocos.SCActionsASTSCExitActionCoCo;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._cocos.SCBasisASTSCStateCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Contains subclasses that Inform users that they use statechart model elements that currently are not supported by
 * MontiArc.
 */
public interface UnsupportedAutomatonElements {

  /** Using final states in automatas is not supported by MontiArc. */
  class FinalStates implements SCBasisASTSCStateCoCo {

    @Override
    public void check(@NotNull ASTSCState node) {
      Preconditions.checkNotNull(node);
      if(node.getSCModifier().isFinal()) {
        Log.warn(ArcError.UNSUPPORTED_MODEL_ELEMENT.format("final states"),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
        );
      }
    }
  }
}
