/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.TypeParameters
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class Result {
    def static generateResult(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      package «comp.packageName»;
      
      «Imports.print(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IResult;
      
      
      public class «comp.name»Result«TypeParameters.printFormalTypeParameters(comp)»   
      «IF comp.superComponent.present» extends 
      «comp.superComponent.get.fullName»Result «IF comp.superComponent.get.hasFormalTypeParameters»< «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
          «scTypeParams»«ENDFOR»>«ENDIF»
      «ENDIF»
      implements IResult 
       {
        
        «FOR port : comp.outgoingPorts»
            «Member.print(helper.getRealPortTypeString(port), port.name, "private")»
        «ENDFOR»
        
        public «comp.name»Result() {
          «IF comp.superComponent.isPresent»
            super();
          «ENDIF»
        }
        
        «IF !comp.allOutgoingPorts.empty»
          public «comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR») {
            «IF comp.superComponent.present»
              super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
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
}