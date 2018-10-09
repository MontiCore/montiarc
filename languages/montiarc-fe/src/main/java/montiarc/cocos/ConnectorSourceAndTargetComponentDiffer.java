package montiarc.cocos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

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
    Collection<ConnectorSymbol> connectors = componentSymbol.getConnectors();
    
    for (ConnectorSymbol cs : connectors) {
      String connectorSource = cs.getSource();
      String connectorTarget = cs.getTarget();
      
      String sourceInstanceName = componentSymbol.getName();
      String targetInstanceName = componentSymbol.getName();
      
      if (connectorSource.contains(".")) {
        sourceInstanceName = connectorSource.substring(0, connectorSource.lastIndexOf("."));
      }
      if (connectorTarget.contains(".")) {
        targetInstanceName = connectorTarget.substring(0, connectorTarget.lastIndexOf("."));
      }
      
      if (connectorSource.equals(connectorTarget)) {
        Log.error(
            "0xMA075 Source and target port of connector are the same ports.",
            cs.getAstNode().get().get_SourcePositionStart());
      }
      
      else if (sourceInstanceName.equals(targetInstanceName)) {
        Log.error(
            "0xMA075 Source and target port of connector are " +
                "ports from the same component." + targetInstanceName,
            cs.getAstNode().get().get_SourcePositionStart());
      }
      
    }
  }
  
}
