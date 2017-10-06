/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/42

/**
 * Implementation of R7: The source port of a simple connector must exist in the subcomponents type
 *
 * @author Crispin Kirchner
 */
public class SimpleConnectorSourceExists implements MontiArcASTComponentCoCo {
  
  /**
   * TODO: either check why ConnectorSymbol has no proper value for sourcePosition, or reimplement
   * using
   * 
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol symbol = (ComponentSymbol) node.getSymbol().orElse(null);
    
    if (null == symbol) {
      Log.error(String.format("0x9AF6C ASTComponent node \"%s\" has no symbol. Did you forget to "
          + "run the SymbolTableCreator before checking cocos?", node.getName()));
      return;
    }
    
    for (ComponentInstanceSymbol instanceSymbol : symbol.getSubComponents()) {
      for (ConnectorSymbol connectorSymbol : instanceSymbol.getSimpleConnectors()) {
        
        ComponentSymbolReference typeReference = instanceSymbol.getComponentType();
        
        if (!typeReference.existsReferencedSymbol()) {
          Log.error(String.format("0xBEA8B The component type \"%s\" can't be resolved.",
              typeReference.getFullName()));
          return;
        }
        
        ComponentSymbol sourceComponent = typeReference.getReferencedSymbol();
        String sourcePort = connectorSymbol.getSource();
        
        Optional<PortSymbol> outgoingPort = sourceComponent.getOutgoingPort(sourcePort);
        
        if (!outgoingPort.isPresent()) {
          Log.error(String.format("0xF4D71 Out port \"%s\" is not present in component \"%s\".",
              sourcePort, sourceComponent.getName()),
              connectorSymbol.getSourcePosition());
        }
      }
    }
  }
  
}
