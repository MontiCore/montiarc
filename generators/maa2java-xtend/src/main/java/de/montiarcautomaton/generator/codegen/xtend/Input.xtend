/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

/**
 * Generates the input class for a component.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class Input {
    def static generateInput(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    
    return '''
      «Utils.printPackage(comp)»
      
      «Utils.printImports(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IInput;
      
      
      public class «comp.name»Input«Utils.printFormalTypeParameters(comp)»
      «IF comp.superComponent.present» extends 
            «Utils.printSuperClassFQ(comp)»Input
            «IF comp.superComponent.get.hasFormalTypeParameters»<
            «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
                «scTypeParams»
                «ENDFOR» > «ENDIF»
            «ENDIF»
      implements IInput 
       {
        
        «FOR port : comp.incomingPorts»
          «Utils.printMember(helper.getRealPortTypeString(port), port.name, "protected")»
        «ENDFOR»
        
        public «comp.name»Input() {
          «IF comp.superComponent.isPresent»
            super();
          «ENDIF»
        }
        
        «IF !comp.allIncomingPorts.empty»
          public «comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR») {
            «IF comp.superComponent.present»
              super(«FOR port : comp.superComponent.get.allIncomingPorts» «port.name» «ENDFOR»);
            «ENDIF»
            «FOR port : comp.incomingPorts»
              this.«port.name» = «port.name»; 
            «ENDFOR»
            
          }
        «ENDIF»
        
        «FOR port : comp.incomingPorts»
          public «helper.getRealPortTypeString(port)» get«port.name.toFirstUpper»() {
            return this.«port.name»;
          }
        «ENDFOR»
        
        @Override
        public String toString() {
          String result = "[";
          «FOR port : comp.incomingPorts»
            result += "«port.name»: " + this.«port.name» + " ";
          «ENDFOR»
          return result + "]";
        }  
        
      } 
      
    '''
  }
}
