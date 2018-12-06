/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.helper.ComponentHelper

import de.monticore.ast.ASTCNode
import de.monticore.codegen.mc2cd.TransformationHelper
import de.monticore.io.FileReaderWriter
import de.monticore.io.paths.IterablePath
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTBehaviorElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import montiarc._symboltable.ComponentSymbol

class ComponentGenerator {
  

  def generateAll(File targetPath, File hwc, ComponentSymbol comp) {
    
    var boolean existsHWCClass = TransformationHelper.existsHandwrittenClass(IterablePath.from(hwc, ".java"), comp.packageName+ "." + comp.name);
    
    toFile(targetPath, comp.name + "Input", generateInput(comp));
    toFile(targetPath, comp.name + "Result", generateResult(comp));
    if (comp.isAtomic) {
      toFile(targetPath, comp.name, generateAtomicComponent(comp));
      if(!existsHWCClass) {
        toFile(targetPath, comp.name + "Impl", generateBehaviorImplementation(comp));
      }
    } else {
      toFile(targetPath, comp.name, generateComposedComponent(comp));
    }

    if (comp.getStereotype().containsKey("deploy")) {
      toFile(targetPath, "Deploy" + comp.name, generateDeploy(comp));
    }
    

  }

  def toFile(File targetPath, String name, String content) {
    var Path path = Paths.get(targetPath.absolutePath + "\\" + name + ".java")
    var FileReaderWriter writer = new FileReaderWriter()
    println("Writing to file " + path + ".");
    writer.storeInFile(path, content)
  }

  def dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp) {
    return JavaPGenerator.newInstance.generate(comp)
  }

  def dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp) {
    return AutomatonGenerator.newInstance.generate(comp)
  }

  def generateBehaviorImplementation(ComponentSymbol comp) {
    var compAST = comp.astNode.get as ASTComponent
    var boolean hasBehavior = false
    for (element : compAST.body.elementList) {
      if (element instanceof ASTBehaviorElement) {
        hasBehavior = true;
        return generateBehavior(element as ASTCNode, comp)
      }
    }
    
    if(!hasBehavior) {
      return generateAbstractAtomicImplementation(comp)
    }

  }
  
  def generateAbstractAtomicImplementation(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp);
    return 
    '''
    package «comp.packageName»;
    
    import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
    «printImports(comp)»
    
    class «comp.name»Impl«printGenerics(comp)»     
    implements IComputable<«comp.name»Input«printGenerics(comp)», «comp.name»Result«printGenerics(comp)»> {
    
      public «comp.name»Impl(«FOR param : comp.configParameters SEPARATOR ','» «helper.getParamTypeName(param)» «param.name» «ENDFOR») {
        throw new Error("Invoking constructor on abstract implementation «comp.packageName».«comp.name»");
      }
    
      public «comp.name»Result«printGenerics(comp)» getInitialValues() {
        throw new Error("Invoking getInitialValues() on abstract implementation «comp.packageName».«comp.name»");
      }
     public «comp.name»Result«printGenerics(comp)» compute(«comp.name»Input«printGenerics(comp)» «helper.inputName») {
        throw new Error("Invoking compute() on abstract implementation «comp.packageName».«comp.name»");
    }
    
    }
    '''
  }
  
  def printGenerics(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
    «IF helper.isGeneric»
    <
      «FOR generic : helper.genericParameters SEPARATOR ','»
        «generic»
      «ENDFOR»
    >
    «ENDIF»
    '''
  }

  def generateInput(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    
    return '''
      package «comp.packageName»;
      
      «printImports(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IInput;
      
      
      public class «comp.name»Input«printGenerics(comp)»
      «IF comp.superComponent.present» extends 
            «comp.superComponent.get.fullName»Input
            «IF helper.isSuperComponentGeneric»<
            «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
                «scTypeParams»
                «ENDFOR» > «ENDIF»
            «ENDIF»
      implements IInput 
       {
        
        «FOR port : comp.incomingPorts»
          private «helper.getRealPortTypeString(port)» «port.name»;
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

  def generateResult(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      package «comp.packageName»;
      
      «printImports(comp)»
      import de.montiarcautomaton.runtimes.timesync.implementation.IResult;
      
      
      public class «comp.name»Result«printGenerics(comp)»   
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
    var ComponentHelper helper = new ComponentHelper(comp);
    
    return '''
      package «comp.packageName»;
      
      «printImports(comp)»
      import «comp.packageName».«comp.name»Input;
      import «comp.packageName».«comp.name»Result;
      import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
      import de.montiarcautomaton.runtimes.timesync.delegation.Port;
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      import de.montiarcautomaton.runtimes.Log;
      
      public class «comp.name»«printGenerics(comp)»      
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName» 
        «IF helper.isSuperComponentGeneric»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
          «scTypeParams»
        «ENDFOR»>«ENDIF»
      «ENDIF»
      implements IComponent {
        
        // component variables
        «FOR v : comp.variables»
          «printMember(helper.printVariableTypeName(v), v.name, "protected")»
        «ENDFOR»
        
        // config parameters
        «FOR param : comp.configParameters»
          «printMember(helper.printParamTypeName(param), param.name, "private final")»
        «ENDFOR»
        
        // port fields
        «FOR port : comp.ports»
          «printMember("Port<" + helper.printPortType(port)+">", port.name, "protected")»
        «ENDFOR»      
      
        // port setter
        «FOR inPort : comp.ports»
          public void setPort«inPort.name.toFirstUpper»(Port<«helper.printPortType(inPort)»> port) {
            this.«inPort.name» = port;
          }
      
        // port getter
          public Port<«helper.printPortType(inPort)»> getPort«inPort.name.toFirstUpper»() {
            return this.«inPort.name»;
          }
        «ENDFOR»
        
        // the components behavior implementation
        private final IComputable
        <«comp.name»Input«printGenerics(comp)», «comp.name»Result«printGenerics(comp)»>
        «helper.behaviorImplName»;      
      
        
        public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','» «helper.getParamTypeName(param)» «param.name»«ENDFOR») {
          «IF comp.superComponent.isPresent»
          super(«FOR inhParam : helper.getInheritedParams() SEPARATOR ','» «inhParam» «ENDFOR»);
          «ENDIF»
          «helper.behaviorImplName» = new «comp.name»Impl«printGenerics(comp)»(
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
        
       private void setResult(«comp.name»Result«printGenerics(comp)» result) {
       «FOR portOut : comp.outgoingPorts»
        this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
       «ENDFOR»
       
       }
      
        @Override
        public void compute() {
          // collect current input port values
        final «comp.name»Input«printGenerics(comp)» input = new «comp.name»Input«printGenerics(comp)»
        («FOR inPort : comp.allIncomingPorts SEPARATOR ','»this.getPort«inPort.name.toFirstUpper»().getCurrentValue()«ENDFOR»);
        
        try {
        // perform calculations
          final «comp.name»Result«printGenerics(comp)» result = «helper.behaviorImplName».compute(input); 
          
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
        final «comp.name»Result«printGenerics(comp)» result = «helper.behaviorImplName».getInitialValues();
        
        // set results to ports
        setResult(result);
        }
        
      }
    '''
  }

  def generateComposedComponent(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    return '''
      package «comp.packageName»;
      
      «printImports(comp)»
      
      import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
      import de.montiarcautomaton.runtimes.timesync.delegation.Port;
      
      public class «comp.name»«printGenerics(comp)»
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»«ENDIF»
      implements IComponent {
       
        // port fields
        «FOR port : comp.ports»
            protected Port<«helper.printPortType(port)»> «port.name»;
           // port setter
           public void setPort«port.name.toFirstUpper»(Port<«helper.printPortType(port)»> port) {
             this.«port.name» = port;
           }
           
           // port getter
           public Port<«helper.printPortType(port)»> getPort«port.name.toFirstUpper»() {
             return this.«port.name»;
           }
        «ENDFOR»   
      
        
        // config parameters
        «FOR param : comp.configParameters»
          private final «helper.printParamTypeName(param)» «param.name»;
        «ENDFOR»
        
        // subcomponents
        «FOR subcomp : comp.subComponents»
          private «helper.getSubComponentTypeName(subcomp)» «subcomp.name»;  
        «ENDFOR»
      
        // subcomponent getter
        «FOR subcomp : comp.subComponents»
          public «helper.getSubComponentTypeName(subcomp)» getComponent«subcomp.name.toFirstUpper»() {
            return this.«subcomp.name»;
          }
        «ENDFOR»
        
        public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','»«helper.printParamTypeName(param)» «param.name»«ENDFOR») {
          «IF comp.superComponent.present»
            super();
          «ENDIF»
          «FOR param : comp.configParameters»
            this.«param.name» = «param.name»;
          «ENDFOR»
        }
      
        «printInit(comp)»
        «printSetup(comp)»
        «printUpdate(comp)»
        
      
        @Override
        public void compute() {
          // trigger computation in all subcomponent instances
          «FOR subcomponent : comp.subComponents»
            this.«subcomponent.name».compute();
          «ENDFOR»
        }
      
      }
        
    '''
  }
  
  def printMember(String type, String name, String visibility) {
    return 
    '''
    «visibility» «type» «name»;
    '''
  }
  
  def printGetter(String type, String name, String methodPostfix) {
    return 
    '''
    public «type» get«methodPostfix»() {
      return this.«name»;
    }
    '''
  }
  
  def printSetter(String type, String name,  String methodPostfix) {
    return 
    '''
    public void set«methodPostfix»(«type» «name») {
      this.«name» = «name»;
    }
    '''
  }
  
  def printInit(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    return 
    '''
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
        «IF helper.isIncomingPort(comp, connector, false)»
          «helper.getConnectorComponentName(connector, false)».setPort«helper.getConnectorPortName(connector, false).toFirstUpper»(«helper.getConnectorComponentName(connector,true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
        «ENDIF»
      «ENDFOR» 
      
      // init all subcomponents
      
      «FOR subcomponent : comp.subComponents»
        this.«subcomponent.name».init();
      «ENDFOR»
      }
    '''
  }
  
  def printUpdate(ComponentSymbol comp) {
    return 
    '''
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
  
  
  def printSetup(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    
    return 
    '''
    @Override
      public void setUp() {
      «IF comp.superComponent.present»
        super.setUp();
      «ENDIF»
      // instantiate all subcomponents
      «FOR subcomponent : comp.subComponents»
        this.«subcomponent.name» = new «helper.getSubComponentTypeName(subcomponent)»(
        «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
          «param»
        «ENDFOR»);
        
      «ENDFOR»
    
        //set up all sub components  
        «FOR subcomponent : comp.subComponents»
      this.«subcomponent.name».setUp();
        «ENDFOR»
        
        // set up output ports
        «FOR portOut : comp.outgoingPorts»
      this.«portOut.name» = new Port<«helper.printPortType(portOut)»>();
        «ENDFOR»
        
        // propagate children's output ports to own output ports
        «FOR connector : comp.connectors»
      «IF !helper.isIncomingPort(comp,connector, false)»
        «helper.getConnectorComponentName(connector,false)».setPort«helper.getConnectorPortName(connector,false).toFirstUpper»(«helper.getConnectorComponentName(connector, true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
      «ENDIF»
        «ENDFOR»
        
      }
    '''
  }

  def printImports(ComponentSymbol comp) {
    return 
    '''
    «FOR _import : comp.imports»
      import «_import.statement»«IF _import.isStar».*«ENDIF»;
    «ENDFOR»
    '''
  }
  
}
