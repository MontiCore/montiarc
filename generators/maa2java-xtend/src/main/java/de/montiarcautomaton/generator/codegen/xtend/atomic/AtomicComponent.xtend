/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic

import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Getter
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.Setter
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
          «scTypeParams»
        «ENDFOR»>«ENDIF»
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
        private final IComputable
        <«comp.name»Input«generics», «comp.name»Result«generics»>
        «helper.behaviorImplName»;      
      
        
        public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','» «helper.getParamTypeName(param)» «param.name»«ENDFOR») {
          «IF comp.superComponent.isPresent»
          super(«FOR inhParam : helper.getInheritedParams() SEPARATOR ','» «inhParam» «ENDFOR»);
          «ENDIF»
          «helper.behaviorImplName» = new «comp.name»Impl«generics»(
          «IF comp.hasConfigParameters» «FOR param : comp.configParameters SEPARATOR ','» «param.name» «ENDFOR» «ENDIF»);
          
          // config parameters
          «FOR param : comp.configParameters»
        this.«param.name» = «param.name»;
        «ENDFOR»
        }
        
        @Override
        public void setUp() {
        «IF comp.superComponent.present»
          super.setUp();
        «ENDIF»
        // set up output ports
        «FOR portOut : comp.outgoingPorts»
          this.«portOut.name» = new Port<«helper.printPortType(portOut)»>();
        «ENDFOR»
        
        this.initialize();
        }
        
        @Override
        public void init() {
        «IF comp.superComponent.present»
          super.init();
        «ENDIF»
        // set up unused input ports
        «FOR portIn : comp.incomingPorts»
          if (this.«portIn.name» == null) {
            this.«portIn.name» = Port.EMPTY;
          }
        «ENDFOR»
        }
        
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
      
        @Override
        public void update() {
          «IF comp.superComponent.present»super.update();«ENDIF»
      
          // update computed value for next computation cycle in all outgoing ports
          «FOR portOut : comp.outgoingPorts»
            this.«portOut.name».update();
          «ENDFOR»
        }
        
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