/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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
 * Context condition that checks, if source and target of connectors exist. CO3:
 * Unqualified sources or targets in connectors either refer to a port or a
 * subcomponent declaration. R5: The first part of a qualified connector’s
 * source or target must correspond to a subcomponent declared in the current
 * component definition (or in a component type definition of a super component
 * type). R6: The second part of a qualified connector’s source or target must
 * correspond to a port name of the referenced subcomponent determined by the
 * first part. R7: The source port of a simple connector must exist in the
 * subcomponents type. <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class ConnectorSourceAndTargetExist implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbol().get();
    Collection<ConnectorSymbol> connectors = componentSymbol.getConnectors();
    
    for (ConnectorSymbol cs : connectors) {
      String connectorSource = cs.getSource();
      String connectorTarget = cs.getTarget();

      Optional<PortSymbol> source = cs.getSourcePort();
      Optional<PortSymbol> target = cs.getTargetPort();

      if(!source.isPresent()) {
        Log.error(String.format("0xMA066 source port "+connectorSource+" of connector "+cs.getName()+" does not exist.",
            cs.getFullName()), cs.getSourcePosition());
      }
      
      if(!target.isPresent()) {
        Log.error(String.format("0xMA067 target port "+connectorTarget+" of connector "+cs.getName()+" does not exist.",
            cs.getFullName()), cs.getSourcePosition());
      }
      
    }
  }
  
}
