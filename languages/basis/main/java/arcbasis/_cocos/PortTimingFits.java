/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import arcbasis._symboltable.PortSymbol;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

public class PortTimingFits implements ArcBasisASTPortCoCo {

  @Override
  public void check(ASTPort node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
            "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the " +
            "coco?",
        node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());

    PortSymbol symbol = node.getSymbol();
    if (symbol.getTiming().equals(Timing.delayed()) && !symbol.isOutgoing()) {
      Log.error(ArcError.TIMING_DELAYED_WITH_INCOMING_PORT.format(node.getName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
