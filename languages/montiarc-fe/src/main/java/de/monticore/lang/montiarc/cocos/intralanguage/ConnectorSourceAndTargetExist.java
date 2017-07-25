/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos.intralanguage;

import java.util.Collection;
import java.util.Optional;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;

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
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol componentSymbol = (ComponentSymbol) node.getSymbol().get();
    Collection<ConnectorSymbol> connectors = componentSymbol.getConnectors();
    
    for (ConnectorSymbol cs : connectors) {
      String connectorSource = cs.getSource();
      String connectorTarget = cs.getTarget();
      
      Optional<PortSymbol> source = componentSymbol.getSpannedScope()
          .<PortSymbol> resolve(connectorSource, PortSymbol.KIND);
      Optional<PortSymbol> target = componentSymbol.getSpannedScope()
          .<PortSymbol> resolve(connectorTarget, PortSymbol.KIND);
      
      if(!source.isPresent()) {
        Log.error(String.format("0xC0001 source port "+connectorSource+" of connector "+cs.getName()+" does not exist.",
            cs.getFullName()), cs.getSourcePosition());
      }
      
      if(!target.isPresent()) {
        Log.error(String.format("0xC0002 target port "+connectorTarget+" of connector "+cs.getName()+" does not exist.",
            cs.getFullName()), cs.getSourcePosition());
      }
      
    }
  }
  
}
