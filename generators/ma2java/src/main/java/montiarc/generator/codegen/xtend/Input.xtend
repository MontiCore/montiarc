/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montiarc.generator.codegen.xtend.util.Utils
import montiarc.generator.helper.ComponentHelper
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator

/**
 * Generates the input class for a component.
 */
class Input implements IMontiArcGenerator {
      override generate(ComponentTypeSymbol comp) {
      var ComponentHelper helper = new ComponentHelper(comp)
      
      return '''
        «Utils.printPackage(comp)»
        
        «Utils.printImports(comp)»
        import de.montiarc.runtimes.timesync.implementation.IInput;
        
        
        public class «comp.name»Input«Utils.printFormalTypeParameters(comp)»
        «IF comp.isPresentParentComponent» extends
              «Utils.printSuperClassFQ(comp)»Input
              «IF comp.getParent.hasTypeParameter»<
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
            «IF comp.isPresentParentComponent»
              super();
            «ENDIF»
          }
          
          «IF !comp.allIncomingPorts.empty»
            public «comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR») {
              «IF comp.isPresentParentComponent»
                super(«FOR port : comp.getParent.getAllIncomingPorts» «port.name»
                «ENDFOR»);
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
  
  override getArtifactName(ComponentTypeSymbol comp) {
    return comp.name + "Input"
  }
  
}
