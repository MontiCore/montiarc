/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

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
      
      «Imports.printImports(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IResult;
      
      
      public class «comp.name»Result«Generics.printGenerics(comp)»   
      «IF comp.superComponent.present» extends 
      «comp.superComponent.get.fullName»Result «IF helper.isSuperComponentGeneric»< «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
          «scTypeParams»«ENDFOR»>«ENDIF»
      «ENDIF»
      implements IResult 
       {
        
        «FOR port : comp.outgoingPorts»
          private «helper.getRealPortTypeString(port)» «port.name»;
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
      
      //getter  
      «FOR port : comp.outgoingPorts»
        public «helper.getRealPortTypeString(port)» get«port.name.toFirstUpper»() {
          return this.«port.name»;
        }
      «ENDFOR»
      
        // setter
        «FOR port : comp.outgoingPorts»
        public void set«port.name.toFirstUpper»(«helper.getRealPortTypeString(port)» «port.name») {
          this.«port.name» = «port.name»;
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