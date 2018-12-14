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
class Setter {
    
  def static print(String type, String name,  String methodPostfix) {
    return 
    '''
    public void set«methodPostfix»(«type» «name») {
      this.«name» = «name»;
    }
    '''
  }
  
}