/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that source and target port are not the same.
 */
public class ConnectorSourceAndTargetDiffer implements ArcBasisASTConnectorCoCo {

  @Override
  public void check(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);
    node.streamTarget().forEach(target -> {
      if (target.deepEquals(node.getSource())) {
        Log.error(String.format(ArcError.CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL.toString(),
          target.getQName(), node.getSourceName()), node.get_SourcePositionStart()
        );
      }
    });
  }
}