/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol


class DynamicSubcomponents {

  def static String print(ComponentSymbol comp) {
    return '''
			«FOR subcomponent : comp.subComponents»
				protected IDynamicComponent «subcomponent.name»;
				protected IDynamicComponent new«subcomponent.name»;
				
				public IDynamicComponent getComponent«subcomponent.name.toFirstUpper»() {
				  return this.«subcomponent.name»;
				}
			«ENDFOR»
		'''
  }
}
