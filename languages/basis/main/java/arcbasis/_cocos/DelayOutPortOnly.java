/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcPort;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that if a port has a delay, then is an out port.
 */
public class DelayOutPortOnly implements ArcBasisASTArcPortCoCo {

  @Override
  public void check(@NotNull ASTArcPort node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isDelayed() && !node.getSymbol().isOutgoing()) {
      Log.error(ArcError.IN_PORT_DELAYED.format(node.getName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
