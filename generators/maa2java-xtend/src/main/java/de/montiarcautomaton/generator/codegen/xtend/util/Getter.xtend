/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class Getter {
    def static print(String type, String name, String methodPostfix) {
    return 
    '''
    public «type» get«methodPostfix»() {
      return this.«name»;
    }
    '''
  }
}