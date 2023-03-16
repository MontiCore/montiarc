/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;


/**
 * Checks that source and target ports of connectors to not belong to the same component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements ArcBasisASTConnectorCoCo {

  @Override
  public void check(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);

    ASTPortAccess connectorSource = node.getSource();
    node.forEachTarget(connectorTarget -> {
      String sourceInstanceName = connectorSource.isPresentComponent() ? connectorSource.getComponent() : "";
      String targetInstanceName = connectorTarget.isPresentComponent() ? connectorTarget.getComponent() : "";

      if (sourceInstanceName.equals(targetInstanceName)) {
        Log.error(ArcError.SOURCE_AND_TARGET_SAME_COMPONENT.format(), node.get_SourcePositionStart());
      }
    });
  }
}