/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.composed

import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Init
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.Setup
import de.montiarcautomaton.generator.codegen.xtend.util.Update
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Setter
import de.montiarcautomaton.generator.codegen.xtend.util.Getter
import de.montiarcautomaton.generator.codegen.xtend.util.ConfigurationParameters

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class ComposedComponent {
  

  def static generateComposedComponent(ComponentSymbol comp) {
    var String generics = Generics.printGenerics(comp)
    var helper = new ComponentHelper(comp);
    return '''
    package «comp.packageName»;
    
    «Imports.printImports(comp)»    
    import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
    import de.montiarcautomaton.runtimes.timesync.delegation.Port;
    
    public class «comp.name»«generics»
    «IF comp.superComponent.present» extends «comp.superComponent.get.fullName»«ENDIF»
    implements IComponent {

      //ports
      «FOR port : comp.ports»
        «Member.printMember("Port<" + helper.printPortType(port)+">", port.name, "protected")»
        
        «Getter.printGetter("Port<" + helper.printPortType(port) + ">", port.name, "Port" + port.name.toFirstUpper)»
        «Setter.printSetter("Port<" + helper.printPortType(port) + ">", port.name, "Port" + port.name.toFirstUpper)»      
        
      «ENDFOR»   
      
      
      // config parameters
      «FOR param : comp.configParameters»
        «Member.printMember(helper.printParamTypeName(param), param.name, "private final")»
      «ENDFOR»
      
      // subcomponents
      «FOR subcomp : comp.subComponents»
        «Member.printMember(helper.getSubComponentTypeName(subcomp), subcomp.name, "private")»
        
        «Getter.printGetter(helper.getSubComponentTypeName(subcomp), subcomp.name, "Component" + subcomp.name.toFirstUpper)»
      «ENDFOR»
      
      public «comp.name»(«ConfigurationParameters.print(comp)») {
        «IF comp.superComponent.present»
          super();
        «ENDIF»
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»;
        «ENDFOR»
      }
      
      «Init.printInit(comp)»
      «Setup.printSetup(comp)»
      «Update.printUpdate(comp)»
      
      
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
}