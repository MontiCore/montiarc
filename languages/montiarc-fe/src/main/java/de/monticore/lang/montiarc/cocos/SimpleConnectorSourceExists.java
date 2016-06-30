/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import java.util.Optional;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Implementation of R7
 *
 * @author Crispin Kirchner
 */
public class SimpleConnectorSourceExists implements MontiArcASTComponentCoCo {
  
  /**
   * TODO: either check why ConnectorSymbol has no proper value for sourcePosition, or reimplement
   * using
   * 
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponent)
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
