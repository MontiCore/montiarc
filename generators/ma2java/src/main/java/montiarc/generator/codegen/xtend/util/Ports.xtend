/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import montiarc.generator.helper.ComponentHelper
import java.util.Collection
import arcbasis._symboltable.PortSymbol

/**
 * Prints the attributes and getter and setter for ports.
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
