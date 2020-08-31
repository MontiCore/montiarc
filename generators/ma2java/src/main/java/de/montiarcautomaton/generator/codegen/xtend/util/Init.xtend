/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import de.montiarcautomaton.generator.helper.ComponentHelper

/**
 * Class responsible for printing the init() method for both atomic and composed components.
 */
class Init {
  
  /**
   * Delegates to the right printInit method.
   */
  def print(ComponentTypeSymbol comp) {
    if (comp.isAtomic) {
    	return printInitAtomic(comp)
    } else {
      return printInitComposed(comp)
    }
  }
  
  def private printInitAtomic(ComponentTypeSymbol comp) {
    return
    '''
    @Override
    public void init() {
    «IF comp.isPresentParentComponent»
      super.init();
    «ENDIF»
    // set up unused input ports
    «FOR portIn : comp.incomingPorts»
      if (this.«portIn.name» == null) {
        this.«portIn.name» = Port.EMPTY;
      }
    «ENDFOR»
    }
    '''
  }
  
  def private printInitComposed(ComponentTypeSymbol comp) {
    var helper = new ComponentHelper(comp);
    
    return 
    '''
    @Override
    public void init() {
    «IF comp.isPresentParentComponent»
      super.init();
    «ENDIF»
    // set up unused input ports
    «FOR portIn : comp.incomingPorts»
      if (this.«portIn.name» == null) {
        this.«portIn.name» = Port.EMPTY;
      }
    «ENDFOR»
    
    // connect outputs of children with inputs of children, by giving 
    // the inputs a reference to the sending ports
    «FOR ASTConnector connector : comp.getAstNode().getConnectors()»
          «FOR ASTPortAccess target : connector.getTargetList»
            «IF helper.isIncomingPort(comp, connector.source, target, false)»
              «helper.getConnectorComponentName(connector.source, target, false)».setPort«helper.getConnectorPortName(connector.source, target, false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target,true)».getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
            «ENDIF»
          «ENDFOR»
    «ENDFOR» 
    
    // init all subcomponents
    
    «FOR subcomponent : comp.subComponents»
      this.«subcomponent.name».init();
    «ENDFOR»
    }
    '''
  }
  
  
}