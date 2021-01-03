/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol

/**
 * Prints the update() method for both atomic and composed components.
 */
class Update {
  
  /**
   * Delegates to the right print method.
   */
  def print(ComponentTypeSymbol comp) {
    if (comp.isDecomposed) {
    	return printUpdateComposed(comp)
    } else {
    	return printUpdateAtomic(comp)
    }
  }
  
  
  def private printUpdateComposed(ComponentTypeSymbol comp){
    return 
    '''
    @Override
    public void update() {
      // update subcomponent instances
      «IF comp.isPresentParentComponent»
        super.update();
      «ENDIF»
      «FOR subcomponent : comp.subComponents»
        this.«subcomponent.name».update();
      «ENDFOR»
    }
    '''
  }
  
  def private printUpdateAtomic(ComponentTypeSymbol comp){
    return 
    '''
    @Override
    public void update() {
      «IF comp.isPresentParentComponent»
      super.update();
      «ENDIF»
    
      // update computed value for next computation cycle in all outgoing ports
      «FOR portOut : comp.outgoingPorts»
      this.«portOut.name».update();
      «ENDFOR»
    }
    '''
  }
}
