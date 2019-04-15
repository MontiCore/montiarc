/*

 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import montiarc._symboltable.ComponentSymbol

/**
 * Generates the deployment class for a component.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class DynamicDeploy {
    def static generateDeploy(ComponentSymbol comp) {
    var name = comp.name;
    var lowname = name.toFirstLower;
    return '''
			package «comp.packageName»;
			
			import java.util.List;
			import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
			import de.montiarcautomaton.runtimes.timesync.delegation.Port; 
			import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
			import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
			     
			
			public class DynamicDeploy«name» {      	
			  final static int CYCLE_TIME = 50; // in ms
			  
			  //Insert own Store and class path here
			  static String STOREPATH = "";
			  static String CLASSPATH = "";
			    
			  public static void main(String[] args) {
			    LoaderManager loman = new LoaderManager((ILoader) new FileSystemLoader());
			    final Dynamic«name» cmp = new Dynamic«name»();
			    
			    cmp.setUp();
			    cmp.setLoaderConfiguration("«lowname»", STOREPATH, CLASSPATH, loman);
			    cmp.init();
			             
			    long time;
			    List<Port> changedPorts;
			       
			    while (!Thread.interrupted()) {
			      time = System.currentTimeMillis();
			      cmp.compute();
			      changedPorts = cmp.reconfigure();
			      cmp.propagatePortChanges(changedPorts);
			      cmp.checkForCmp();
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
