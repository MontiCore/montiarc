/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.montiarcautomaton.generator.codegen.xtend.util.IMontiArcGenerator

/**
 * Generates the result class for a component.
 */
class Result implements IMontiArcGenerator {
    override generate(ComponentTypeSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      «Utils.printPackage(comp)»
      
      «Utils.printImports(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IResult;
      
      
      public class «comp.name»Result«Utils.printFormalTypeParameters(comp)»   
      «IF comp.isPresentParentComponent» extends
      «Utils.printSuperClassFQ(comp)»Result
      «IF comp.getParent.getLoadedSymbol.hasTypeParameter»< «FOR scTypeParams : helper
      .superCompActualTypeArguments SEPARATOR ','»
          «scTypeParams»«ENDFOR»>«ENDIF»
      «ENDIF»
      implements IResult 
       {
        
        «FOR port : comp.outgoingPorts»
            «Utils.printMember(helper.getRealPortTypeString(port), port.name, "private")»
        «ENDFOR»
        
        public «comp.name»Result() {
          «IF comp.isPresentParentComponent»
            super();
          «ENDIF»
        }
        
        «IF !comp.allOutgoingPorts.empty»
          public «comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR») {
            «IF comp.isPresentParentComponent»
              super(«FOR port : comp.getParent.getLoadedSymbol.getAllOutgoingPorts» «port.name»
              «ENDFOR»);
            «ENDIF»
            «FOR port : comp.outgoingPorts»
              this.«port.name» = «port.name»; 
            «ENDFOR»
            
          }
        «ENDIF»
      
        «FOR port : comp.outgoingPorts»
          «var name = port.name»
          «var type = helper.getRealPortTypeString(port)»
          public void set«name.toFirstUpper»(«type» «name») {
            this.«name» = «name»;
          }
          
          public «type» get«name.toFirstUpper»() {
            return this.«name»;
          }
        «ENDFOR»
      
        @Override
        public String toString() {
          String result = "[";
          «FOR port : comp.outgoingPorts»
            result += "«port.name»: " + this.«port.name» + " ";
          «ENDFOR»
          return result + "]";
        }  
        
      } 
      
    '''
  }
  
  override getArtifactName(ComponentTypeSymbol comp) {
    return comp.name + "Result"
  }
  
}
