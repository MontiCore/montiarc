package montiarc.cocos;

import java.util.Collection;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

/**
 * Checks whether the components of the source and target ports are not the same component.
 */
public class ConnectorSourceAndTargetComponentDiffer implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    Collection<ConnectorSymbol> connectors = componentSymbol.getConnectors();
    
    for (ConnectorSymbol cs : connectors) {
      String connectorSource = cs.getSource();
      String connectorTarget = cs.getTarget();


      Optional<PortSymbol> source = componentSymbol.getSpannedScope()
          .<PortSymbol> resolveDown(connectorSource, PortSymbol.KIND);
      Optional<PortSymbol> target = componentSymbol.getSpannedScope()
          .<PortSymbol> resolveDown(connectorTarget, PortSymbol.KIND);

      if (connectorSource.equals(connectorTarget)) {
        Log.error(
            "0xMA075 Source and target port of connector are ports " +
                "from the same component.",
            cs.getAstNode().get().get_SourcePositionStart());
      }

      else if (source.isPresent()) {
        if (target.isPresent())
          if (source.get().getEnclosingScope().equals(target.get().getEnclosingScope())) {
            Log.error(
                "0xMA075 Source and target port of connector are " +
                    "ports from the same component.",
                source.get().getAstNode().get().get_SourcePositionStart());
          }
        
      }
    }
  }
}
