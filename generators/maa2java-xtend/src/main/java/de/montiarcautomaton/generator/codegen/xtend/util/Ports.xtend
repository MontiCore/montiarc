/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import java.util.Collection
import montiarc._symboltable.PortSymbol

/**
 * Prints the attributes and getter and setter for ports.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class Ports {
  
  def String print(Collection<PortSymbol> ports) {
    
    return 
    '''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortTypeString(port.component.get, port)»
    «var name = port.name»
    
    protected Port<«type»> «name»;
    
    public Port<«type»> getPort«name.toFirstUpper»() {
          return this.«name»;
    }
    
    public void setPort«name.toFirstUpper»(Port<«type»> «name») {
          this.«name» = «name»;
    }
    
    «ENDFOR»
    '''
  } 
}
