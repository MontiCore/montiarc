/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTConnector;
import arc._symboltable.ComponentSymbol;
import arc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that source and target ports of connectors to not belong to the same component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements ArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentSymbol componentSymbol = node.getSymbol();
    List<ASTConnector> connectors = node.getConnectors();
    connectors.forEach(connector -> {
      ASTMCQualifiedName connectorSource = connector.getSource().getQualifiedName();
      connector.forEachTargets(connectorTarget -> {
        String sourceInstanceName = componentSymbol.getName();
        String targetInstanceName = componentSymbol.getName();
        if (connectorSource.sizeParts() > 1) {
          sourceInstanceName = String
            .join(".", connectorSource.subListParts(0, connectorSource.sizeParts() - 1));
        }
        if (connectorTarget.getQualifiedName().sizeParts() > 1) {
          targetInstanceName = String
            .join(".", connectorTarget.getQualifiedName().subListParts(0,
              connectorTarget.getQualifiedName().sizeParts() - 1));
        }

        if (connectorSource.equals(connectorTarget.getQualifiedName())) {
          Log.error(String.format(ArcError.SOURCE_AND_TARGET_SAME_PORT.toString(),
            componentSymbol.getFullName()), connector.get_SourcePositionStart());
        } else if (sourceInstanceName.equals(targetInstanceName)) {
          Log.error(String.format(ArcError.SOURCE_AND_TARGET_SAME_COMPONENT.toString(),
            componentSymbol.getFullName()), connector.get_SourcePositionStart());
        }
      });
    });
  }
}