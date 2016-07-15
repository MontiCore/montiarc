/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.trafos.expandcomponents;

import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._visitor.MontiArcInheritanceVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * TODO MvW<-RH: Implement as trafo
 *
 * @author Robert Heim
 */
public class ExpandComponentsTrafo implements MontiArcInheritanceVisitor {
  private MontiArcExpandedComponentInstanceSymbolCreator instanceSymbolCreator = new MontiArcExpandedComponentInstanceSymbolCreator();
  
  public void endVisit(ASTMACompilationUnit node) {
    // creates all instances which are created through the top level component
    
    instanceSymbolCreator.createInstances(
        (ComponentSymbol) (Log.errorIfNull(node.getComponent().getSymbol().orElse(null))));
        
  }
}
