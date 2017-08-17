/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.trafos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.ComponentSymbol;
import montiarc._visitor.MontiArcInheritanceVisitor;

/**
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
