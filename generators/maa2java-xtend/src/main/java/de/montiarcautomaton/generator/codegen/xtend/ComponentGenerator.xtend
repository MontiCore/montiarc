/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import java.io.File
import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTBehaviorElement
import de.montiarcautomaton.generator.helper.ComponentHelper

class ComponentGenerator {

  def generateAll(File targetPath, File hwcPath, ComponentSymbol compSym) {
  }

  def generateInput(ComponentSymbol comp) {
    return '''
      package «comp.packageName»;
      
      «FOR import : comp.imports»
        import «import»;
      «ENDFOR»
      
      class «comp.name»Input 
      «IF !comp.formalTypeParameters.empty» < «FOR generic : comp.formalTypeParameters SEPARATOR ','» «generic.name» «ENDFOR»>«ENDIF»
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»Input«ENDIF»
      implements IInput 
       {
        
        «FOR port : comp.incomingPorts»
          private «port.typeReference.referencedSymbol.name» «port.name»;
        «ENDFOR»
        
        public «comp.name»Input() {
          «IF comp.superComponent.isPresent»
            super();
          «ENDIF»
        }
        
        «IF !comp.allIncomingPorts.empty»
          public «comp.name»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» «port.typeReference.referencedSymbol.name» «port.name» «ENDFOR»=) {
            «IF comp.superComponent.present»
              super(«FOR port : comp.superComponent.get.allIncomingPorts» «port.name» «ENDFOR»);
            «ENDIF»
            «FOR port : comp.incomingPorts»
              this.«port.name» = «port.name»; 
            «ENDFOR»
            
          }
        «ENDIF»
        
      «FOR port : comp.incomingPorts»
        public «port.typeReference.referencedSymbol.name» get«port.name.toFirstUpper»() {
          return this.«port.name»;
      «ENDFOR»
      
      @Override
      public String toString() {
        String result = "["
        «FOR port : comp.incomingPorts»
          result += "«port.name»: " + this.«port.name» + " ";
        «ENDFOR»
        return result + "]"
      }  
        
      } 
      
    '''
  }

  def generateResult(ComponentSymbol comp) {
    return '''
      package «comp.packageName»;
      
      «FOR import : comp.imports»
        import «import»;
      «ENDFOR»
      
      class «comp.name»Output 
      «IF !comp.formalTypeParameters.empty» < «FOR generic : comp.formalTypeParameters SEPARATOR ','» «generic.name» «ENDFOR»>«ENDIF»
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»Input«ENDIF»
      implements IOutput 
       {
        
        «FOR port : comp.outgoingPorts»
          private «port.typeReference.referencedSymbol.name» «port.name»;
        «ENDFOR»
        
        public «comp.name»Output() {
          «IF comp.superComponent.isPresent»
            super();
          «ENDIF»
        }
        
        «IF !comp.allOutgoingPorts.empty»
          public «comp.name»Input(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «port.typeReference.referencedSymbol.name» «port.name» «ENDFOR»=) {
            «IF comp.superComponent.present»
              super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
            «ENDIF»
            «FOR port : comp.outgoingPorts»
              this.«port.name» = «port.name»; 
            «ENDFOR»
            
          }
        «ENDIF»
        
      «FOR port : comp.outgoingPorts»
        public «port.typeReference.referencedSymbol.name» get«port.name.toFirstUpper»() {
          return this.«port.name»;
      «ENDFOR»
      
      @Override
      public String toString() {
        String result = "["
        «FOR port : comp.outgoingPorts»
          result += "«port.name»: " + this.«port.name» + " ";
        «ENDFOR»
        return result + "]"
      }  
        
      } 
      
    '''
  }

  def generateBehavior(ASTBehaviorElement behavior) {
  }

  def generateDeploy(ComponentSymbol comp) {
    var name = comp.name;
    return '''
      package «comp.packageName»
      
      public class Deploy«name» {
        final static int CYCLE_TIME = 50; // in ms
          
        public static void main(String[] args) {
          final «name» cmp = new «name»();
          
          cmp.setUp();
          cmp.init();
                   
          long time;
             
          while (!Thread.interrupted()) {
            time = System.currentTimeMillis();
            cmp.compute();
            cmp.update();
            while((System.currentTimeMillis()-time) < CYCLE_TIME){
              Thread.yield();
            }
          }
        }
      }
    '''
  }

  def generateAtomicComponent(ComponentSymbol comp) {
    return '''
      package «comp.packageName»;
      
      import «comp.packageName».«comp.name»Input;
      import «comp.packageName».«comp.name»Result;
      «FOR import : comp.imports»
        import «import.statement»«IF import.isStar».*«ENDIF»;
      «ENDFOR»
      import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
      import de.montiarcautomaton.runtimes.timesync.delegation.Port;
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      import de.montiarcautomaton.runtimes.Log;
      
      public class ${name} 
      «IF !comp.formalTypeParameters.empty» < «FOR generic : comp.formalTypeParameters SEPARATOR ','» «generic.name» «ENDFOR»>«ENDIF»
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»Input«ENDIF»
      implements IComponent {
        
        // component variables
        «FOR v : comp.variables»
          private «v.typeReference.name» «v.name»; 
        «ENDFOR»
        
        // config parameters
        «FOR param : comp.configParameters»
          private final «param.type.name» «param.name»;
        «ENDFOR»
        
        // port fields
        «FOR port : comp.ports»
          protected Port<«port.typeReference.name»> «port.name»;
        «ENDFOR»      
      
        // port setter
        «FOR inPort : comp.incomingPorts»
          public void setPort«inPort.name.toFirstUpper»(Port<«inPort.typeReference.name»> port) {
            this.«inPort.name» = port;
          }
        «ENDFOR»
      
        // port getter
        «FOR outPort : comp.outgoingPorts»
          public Port<«outPort.typeReference.name»> getPort«outPort.name.toFirstUpper»() {
            return this.«outPort.name»;
          }
        «ENDFOR»
        
        // the components behavior implementation
        private final IComputable
        <«comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF»,
        «comp.name»Output«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF»>
        behaviorImpl;      
      
        
        public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','» «param.type.name» «param.name»«ENDFOR») {
          behaviorImpl = new «comp.name»Impl
        «IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»«ENDIF»
          «IF comp.hasConfigParameters» («FOR param : comp.configParameters SEPARATOR ','» «param.name» «ENDFOR») «ENDIF»;
          
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
          this.«portOut.name» = new Port<«portOut.typeReference.name»>();
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
        
       private void setResult(«comp.name»Result«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» result) {
       «FOR portOut : comp.outgoingPorts»
        this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
       «ENDFOR»
       
       }
      
        @Override
        public void compute() {
          // collect current input port values
        final «comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» input = new «comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF»
        input = new «comp.name»Input «IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» input = new «comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF»
        («FOR inPort : comp.incomingPorts SEPARATOR ','»this.«inPort.name».getCurrentValue()«ENDFOR»);
        
        try {
        // perform calculations
          final «comp.name»Result«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» input = new «comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» result =
          behaviorImpl.compute(input); 
          
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
           final «comp.name»Result
        «IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF» input = new «comp.name»Input«IF comp.hasFormalTypeParameters» <«FOR generics : comp.formalTypeParameters SEPARATOR ','»«generics.name»«ENDFOR»>«ENDIF»
        result = behaviorImpl.getInitialValues();
        
        // set results to ports
        setResult(result);
        }
    '''
  }

  def generateComposedComponent(ComponentSymbol comp) {
    var helper = ComponentHelper.newInstance;
    return '''
      package «comp.packageName»;
      
      «FOR import : comp.imports»
        import «import.statement»«IF import.isStar».*«ENDIF»;
      «ENDFOR»
      
      import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
      import de.montiarcautomaton.runtimes.timesync.delegation.Port;
      
      public class «comp.name»
      public class ${name}
      «IF !comp.formalTypeParameters.empty» < «FOR generic : comp.formalTypeParameters SEPARATOR ','» «generic.name» «ENDFOR»>«ENDIF»
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»Input«ENDIF»
      implements IComponent {
       
        // port fields
        «FOR port : comp.ports»»
          protected Port<«port.typeReference.name»> «port.name»;
           p// port setter
           ppublic void setPort«port.name.toFirstUpper»(Port<«port.typeReference.name»> port) {
           p  this.«port.name» = port;
           p}
          
            // port getter
            public Port<«port.typeReference.name»> getPort«port.name.toFirstUpper»() {
              return this.«port.name»;
            }
        «ENDFOR»   
      
        
        // config parameters
        «FOR param : comp.configParameters»
          private final «param.type.name» «param.name»;
        «ENDFOR»
        
        // subcomponents
        «FOR subcomp : comp.subComponents»
          private «subcomp.componentType.name» «subcomp.name»;  
        «ENDFOR»
      
        // subcomponent getter
        «FOR subcomp : comp.subComponents»
        public «subcomp.componentType.name» getComponent«subcomp.name.toFirstUpper»() {
          return this.«subcomp.name»;
        }
        «ENDFOR»
                
        public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','»«param.type.name» «param.name»«ENDFOR») {
          «IF comp.superComponent.present»
          super();
          «ENDIF»
          «FOR param : comp.configParameters»
           this.«param.name» = «param.name»;
          «ENDFOR»
        }

        @Override
        public void setUp() {
          «IF comp.superComponent.present»
          super.setUp();
          «ENDIF»
          // instantiate all subcomponents
          «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name» = new «subcomponent.componentType.name»(
          «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
          «param»
          «ENDFOR»);
          
          )
          «ENDFOR»

          //set up all sub components  
          «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».setUp();
          «ENDFOR»
          
          // set up output ports
          «FOR portOut : comp.outgoingPorts»
          this.«portOut.name» = new Port<«portOut.typeReference.name»>();
          «ENDFOR»
          
          // propagate children's output ports to own output ports
          «FOR connector : comp.connectors»
          «IF !helper.isIncomingPort(comp,connector, false, connector.target)»
            «helper.getConnectorComponentName(connector,false)».setPort(«helper.getConnectorPortName(connector,false).toFirstUpper»(«helper.getConnectorComponentName(connector, true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
          «ENDIF»
          «ENDFOR»
          
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
          
          // connect outputs of children with inputs of children, by giving 
          // the inputs a reference to the sending ports
          «FOR connector : comp.connectors»
          «IF helper.isIncomingPort(comp, connector, false, connector.target)»
          	«helper.getConnectorComponentName(connector, false)».setPort«helper.getConnectorPortName(connector, false).toFirstUpper»(«helper.getConnectorComponentName(connector,true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
          «ENDIF»
          «ENDFOR» 
          
          // init all subcomponents
          
          «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».init();
          «ENDFOR»
        }
      
        @Override
        public void compute() {
          // trigger computation in all subcomponent instances
          «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».compute();
          «ENDFOR»
        }
      
        @Override
        public void update() {
          // update subcomponent instances
          «IF comp.superComponent.present»
          super.update();
          «ENDIF»
          «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».update();
          «ENDFOR»
        }
        
    '''
  }

}
