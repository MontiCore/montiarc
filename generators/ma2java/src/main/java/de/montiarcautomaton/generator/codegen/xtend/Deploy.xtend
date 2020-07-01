/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.codegen.xtend.util.IMontiArcGenerator

/**
 * Generates the deployment class for a component.
 */
class Deploy implements IMontiArcGenerator {
  override generate(ComponentTypeSymbol comp) {
    var name = comp.name;
    return '''
      «Utils.printPackage(comp)»
      
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

  override getArtifactName(ComponentTypeSymbol comp) {
    return "Deploy" + comp.name
  }

}
