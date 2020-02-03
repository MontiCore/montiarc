/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol

/**
 * The implementation class for atomic components without specified behavior.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class AbstractAtomicImplementation {
  def generateAbstractAtomicImplementation(ComponentSymbol comp) {
    var String generics = Utils.printFormalTypeParameters(comp)
    return '''
	  «Utils.printPackage(comp)»
	  
	  import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
	  «Utils.printImports(comp)»

	  class «comp.name»Impl«generics»
	  implements IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> {
	  
	    public «comp.name»Impl(«Utils.printConfiurationParametersAsList(comp)») {
	      throw new Error("Invoking constructor on abstract implementation «comp.packageName».«comp.name»");
	    }
	  
	    public «comp.name»Result«generics» getInitialValues() {
	      throw new Error("Invoking getInitialValues() on abstract implementation «comp.packageName».«comp.name»");
	    }

	    public «comp.name»Result«generics» compute(«comp.name»Input«generics» «Identifier.inputName») {
	      throw new Error("Invoking compute() on abstract implementation «comp.packageName».«comp.name»");
	    }

	  }
    '''
  }
}
