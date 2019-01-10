/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.ConfigurationParameters
import de.montiarcautomaton.generator.codegen.xtend.util.Getter
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Init
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.Setter
import de.montiarcautomaton.generator.codegen.xtend.util.Setup
import de.montiarcautomaton.generator.codegen.xtend.util.TypeParameters
import de.montiarcautomaton.generator.codegen.xtend.util.Update
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.symboltable.types.JFieldSymbol
import java.util.ArrayList
import java.util.List
import montiarc._ast.ASTPort
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.ComponentSymbolReference

class ComponentGenerator {
  var String generics;
  var ComponentHelper helper;

  def generate(ComponentSymbol comp) {
    generics = TypeParameters.printFormalTypeParameters(comp)
    helper = new ComponentHelper(comp);

    return '''
    package «comp.packageName»;
    
    
    «Imports.print(comp)»
    import «comp.packageName».«comp.name»Input;
    import «comp.packageName».«comp.name»Result;
    import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
    import de.montiarcautomaton.runtimes.timesync.delegation.Port;
    import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
    import de.montiarcautomaton.runtimes.Log;
    
      public class «comp.name»«generics»      
      «IF comp.superComponent.present» extends «comp.superComponent.get.fullName» 
              «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
                «scTypeParams»«ENDFOR»>
              «ENDIF»
      «ENDIF»
      implements IComponent {
        
      //ports
      «FOR port : comp.ports»
        «var String portType = ComponentHelper.printTypeName((port.astNode.get as ASTPort).type)»
          «Getter.print("Port<" + portType + ">", port.name, "Port" + port.name.toFirstUpper)»
          «Setter.print("Port<" + portType + ">", port.name, "Port" + port.name.toFirstUpper)»      
      «ENDFOR»   
      «Member.printPorts(comp)»
      
      // component variables
      «Member.printVariables(comp)»
      
      // config parameters
      «Member.printConfigParameters(comp)»

      «IF comp.isDecomposed»
        // subcomponents
        «Member.printSubcomponents(comp)»
        «FOR subcomp : comp.subComponents»
          «Getter.print(ComponentHelper.getSubComponentTypeName(subcomp), subcomp.name, "Component" + subcomp.name.toFirstUpper)»
        «ENDFOR»
        
        «printComputeComposed(comp)»
      «ELSE»
      // the components behavior implementation
      private final IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> «helper.behaviorImplName»;
      
      «printComputeAtomic(comp)»
      private void initialize() {
        // get initial values from behavior implementation
        final «comp.name»Result«generics» result = «helper.behaviorImplName».getInitialValues();
        
        // set results to ports
        setResult(result);
        this.update();
      }
      private void setResult(«comp.name»Result«generics» result) {
        «FOR portOut : comp.outgoingPorts»
          this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
        «ENDFOR»
      }
      «ENDIF»
      
      «Setup.print(comp)»
      
      «Init.print(comp)»
      
      «Update.print(comp)»
      
      «printConstructor(comp)»
      
      }
      
    '''
  }

  def printConstructor(ComponentSymbol comp) {
    return '''
      public «comp.name»(«ConfigurationParameters.print(comp)») {
        «IF comp.superComponent.present»
          super(«FOR inhParam : getInheritedParams(comp) SEPARATOR ','» «inhParam» «ENDFOR»);
        «ENDIF»
        
        «IF comp.isAtomic»
          «helper.behaviorImplName» = new «comp.name»Impl«generics»(
          «IF comp.hasConfigParameters»
            «FOR param : comp.configParameters SEPARATOR ','»
              «param.name»
            «ENDFOR»
          «ENDIF»);
        «ENDIF»
        // config parameters       
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»;
        «ENDFOR»
      }
    '''
  }

  def printComputeAtomic(ComponentSymbol comp) {
    return '''
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
    '''
  }

  def printComputeComposed(ComponentSymbol comp) {
    return '''
      @Override
      public void compute() {
      // trigger computation in all subcomponent instances
        «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».compute();
        «ENDFOR»
      }
    '''
  }
  
  def private static List<String> getInheritedParams(ComponentSymbol component) {
    var List<String> result = new ArrayList;
    var List<JFieldSymbol> configParameters = component.getConfigParameters();
    if (component.getSuperComponent().isPresent()) {
      var ComponentSymbolReference superCompReference = component.getSuperComponent().get();
      var List<JFieldSymbol> superConfigParams = superCompReference.getReferencedSymbol()
          .getConfigParameters();
      if (!configParameters.isEmpty()) {
        for (var i = 0; i < superConfigParams.size(); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }

}
