/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class ConfigurationParameters {
  
  def static print(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR param : comp.configParameters SEPARATOR ','» «helper.getParamTypeName(param)» «param.name» «ENDFOR»
    '''
  }
  
}