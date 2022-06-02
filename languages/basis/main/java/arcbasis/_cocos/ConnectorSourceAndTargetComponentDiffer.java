/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * Checks that source and target ports of connectors to not belong to the same component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol componentTypeSymbol = node.getSymbol();
    List<ASTConnector> connectors = node.getConnectors();
    connectors.forEach(connector -> {
      ASTPortAccess connectorSource = connector.getSource();
      connector.forEachTarget(connectorTarget -> {
        String sourceInstanceName = connectorSource.isPresentComponent()?connectorSource.getComponent():componentTypeSymbol.getName();
        String targetInstanceName = connectorTarget.isPresentComponent()?connectorTarget.getComponent():componentTypeSymbol.getName();

        if (sourceInstanceName.equals(targetInstanceName)) {
          Log.error(ArcError.SOURCE_AND_TARGET_SAME_COMPONENT.format(
            componentTypeSymbol.getFullName()), connector.get_SourcePositionStart());
        }
      });
    });
  }
}