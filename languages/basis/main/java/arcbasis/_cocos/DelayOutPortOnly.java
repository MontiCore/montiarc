/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that if a port has a delay, then is an out port.
 */
public class DelayOutPortOnly implements ArcBasisASTPortCoCo {

  @Override
  public void check(@NotNull ASTPort node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isDelayed() && !node.getSymbol().isOutgoing()) {
      Log.error(ArcError.DELAY_OUT_PORT_ONLY.toString(),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }
}
