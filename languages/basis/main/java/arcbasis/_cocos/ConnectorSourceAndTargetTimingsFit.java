/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that the timing of the source port of a connector matches the timing of the target port.
 */
public class ConnectorSourceAndTargetTimingsFit implements ArcBasisASTConnectorCoCo {

  @Override
  public void check(@NotNull ASTConnector connector) {
    Preconditions.checkNotNull(connector);

    if (!connector.getSource().isPresentPortSymbol()) return;
    ASTPortAccess source = connector.getSource();

    for (ASTPortAccess target : connector.getTargetList()) {
      if (target.isPresentPortSymbol() &&
        !source.getPortSymbol().getTiming().matches(target.getPortSymbol().getTiming())) {
        Log.error(ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH.format(
            target.getPortSymbol().getTiming().getName(),
            source.getPortSymbol().getTiming().getName()
          ),
          target.get_SourcePositionStart(),
          target.get_SourcePositionEnd()
        );
      }
    }
  }
}
