/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.*;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Checks whether the components of the source and target ports are not the same
 * component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    List<ASTConnector> connectors = node.getConnectors();
    connectors.forEach(connector -> {
      ASTQualifiedName connectorSource = connector.getSource();
      connector.forEachTargetss(connectorTarget -> {
        String sourceInstanceName = componentSymbol.getName();
        String targetInstanceName = componentSymbol.getName();
        if (connectorSource.sizeParts() > 1) {
          sourceInstanceName = String
              .join(".", connectorSource.subListParts(0, connectorSource.sizeParts()-1));
        }
        if (connectorTarget.sizeParts() > 1) {
          targetInstanceName = String
              .join(".", connectorTarget.subListParts(0, connectorTarget.sizeParts()-1));
        }
        
        if (connectorSource.equals(connectorTarget)) {
          Log.error(
              "0xMA075 Source and target port of connector are the same ports.",
              connector.get_SourcePositionStart());
        }
        
        else if (sourceInstanceName.equals(targetInstanceName)) {
          Log.error(
              "0xMA075 Source and target port of connector are " +
                  "ports from the same component." + targetInstanceName,
              connector.get_SourcePositionStart());
        }
        
      });
    });
  }
}
