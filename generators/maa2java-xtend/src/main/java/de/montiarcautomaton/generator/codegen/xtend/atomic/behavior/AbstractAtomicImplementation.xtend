/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior

import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Imports
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
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
class AbstractAtomicImplementation {
  def static generateAbstractAtomicImplementation(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp);
    var String generics = Generics.print(comp)
    return '''
      package «comp.packageName»;
      
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      «Imports.print(comp)»
      
      class «comp.name»Impl«generics»     
      implements IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> {
      
        public «comp.name»Impl(«ConfigurationParameters.print(comp)») {
          throw new Error("Invoking constructor on abstract implementation «comp.packageName».«comp.name»");
        }
      
        public «comp.name»Result«generics» getInitialValues() {
          throw new Error("Invoking getInitialValues() on abstract implementation «comp.packageName».«comp.name»");
        }
       public «comp.name»Result«generics» compute(«comp.name»Input«generics» «helper.inputName») {
          throw new Error("Invoking compute() on abstract implementation «comp.packageName».«comp.name»");
      }
      
      }
    '''
  }
}
