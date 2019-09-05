/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.codegen.xtend.Deploy

/**
 * Generates the deployment class for a component.
 */
class DynRecDeploy extends Deploy{
    override generate(ComponentSymbol comp) {
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
			      cmp.reconfigure();
			      cmp.update();
			      while((System.currentTimeMillis()-time) < CYCLE_TIME){
			        Thread.yield();
			      }
			    }
			  }
			}
		'''
  }
}
