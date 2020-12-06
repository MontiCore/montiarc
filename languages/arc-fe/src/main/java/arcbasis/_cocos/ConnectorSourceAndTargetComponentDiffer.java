/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that source and target ports of connectors to not belong to the same component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol componentTypeSymbol = node.getSymbol();
    List<ASTConnector> connectors = node.getConnectors();
    connectors.forEach(connector -> {
      ASTPortAccess connectorSource = connector.getSource();
      connector.forEachTargets(connectorTarget -> {
        String sourceInstanceName = componentTypeSymbol.getName();
        String targetInstanceName = componentTypeSymbol.getName();
        if (connectorSource.isPresentComponent()) {
          sourceInstanceName = connectorSource.getComponent();
        }
        if (connectorTarget.isPresentComponent()) {
          targetInstanceName = connectorTarget.getComponent();
        }

        if (connectorSource.equals(connectorTarget.getQName())) {
          Log.error(String.format(ArcError.SOURCE_AND_TARGET_SAME_PORT.toString(),
            componentTypeSymbol.getFullName()), connector.get_SourcePositionStart());
        } else if (sourceInstanceName.equals(targetInstanceName)) {
          Log.error(String.format(ArcError.SOURCE_AND_TARGET_SAME_COMPONENT.toString(),
            componentTypeSymbol.getFullName()), connector.get_SourcePositionStart());
        }
      });
    });
  }
}