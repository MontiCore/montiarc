/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic

import de.montiarcautomaton.generator.codegen.xtend.util.ConfigurationParameters
import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Getter
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Init
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.Setter
import de.montiarcautomaton.generator.codegen.xtend.util.Setup
import de.montiarcautomaton.generator.codegen.xtend.util.Update
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
class AtomicComponent {
   def static generateAtomicComponent(ComponentSymbol comp) {
     var String generics = Generics.printGenerics(comp)
    var ComponentHelper helper = new ComponentHelper(comp);
    
    return '''
      package «comp.packageName»;
      
      «Imports.printImports(comp)»
      import «comp.packageName».«comp.name»Input;
      import «comp.packageName».«comp.name»Result;
      import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
      import de.montiarcautomaton.runtimes.timesync.delegation.Port;
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      import de.montiarcautomaton.runtimes.Log;
      
      public class «comp.name»«Generics.printGenerics(comp)»      
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName» 
        «IF helper.isSuperComponentGeneric»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
          «scTypeParams»«ENDFOR»>«ENDIF»
      «ENDIF»
      implements IComponent {
        
        // component variables
        «FOR v : comp.variables»
          «Member.printMember(helper.printVariableTypeName(v), v.name, "protected")»
        «ENDFOR»
        
        // config parameters
        «FOR param : comp.configParameters»
          «Member.printMember(helper.printParamTypeName(param), param.name, "private final")»
        «ENDFOR»
        
        // port fields
        «FOR port : comp.ports»
          «Member.printMember("Port<" + helper.printPortType(port)+">", port.name, "protected")»
        «ENDFOR»      
      
        // port setter
        «FOR inPort : comp.ports»
          «Getter.printGetter("Port<" + helper.printPortType(inPort) + ">", inPort.name, "Port" + inPort.name.toFirstUpper)»
          «Setter.printSetter("Port<" + helper.printPortType(inPort) + ">", inPort.name, "Port" + inPort.name.toFirstUpper)»
        «ENDFOR»
        
        // the components behavior implementation
        private final IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> «helper.behaviorImplName»;
      

        public «comp.name»(«ConfigurationParameters.print(comp)») {
          «IF comp.superComponent.isPresent»
          super(«FOR inhParam : helper.getInheritedParams() SEPARATOR ','» «inhParam» «ENDFOR»);
          «ENDIF»
          «helper.behaviorImplName» = new «comp.name»Impl«generics»(
          «IF comp.hasConfigParameters»
            «FOR param : comp.configParameters SEPARATOR ','»
              «param.name»
            «ENDFOR»
          «ENDIF»);
          
          // config parameters
          «FOR param : comp.configParameters»
            this.«param.name» = «param.name»;
          «ENDFOR»
        }
        
        «Setup.printSetup(comp)»
        
        «Init.printInit(comp)»
        
        
        private void setResult(«comp.name»Result«generics» result) {
        «FOR portOut : comp.outgoingPorts»
          this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
        «ENDFOR»
        }
      
        @Override
        public void compute() {
        // collect current input port values
        final «comp.name»Input«generics» input = new «comp.name»Input«generics»
        («FOR inPort : comp.allIncomingPorts SEPARATOR ','»this.getPort«inPort.name.toFirstUpper»().getCurrentValue()«ENDFOR»);
        
        try {
        // perform calculations
          final «comp.name»Result«generics» result = «helper.behaviorImplName».compute(input); 
          
          // set results to ports
          setResult(result);
          } catch (Exception e) {
        Log.error("«comp.name»", e);
          }
        }
      
        «Update.printUpdate(comp)»
        
        private void initialize() {
           // get initial values from behavior implementation
        final «comp.name»Result«generics» result = «helper.behaviorImplName».getInitialValues();
        
        // set results to ports
        setResult(result);
        this.update();
        }
        
      }
    '''
  }
}