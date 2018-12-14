/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.composed

import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.codegen.xtend.util.Init
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