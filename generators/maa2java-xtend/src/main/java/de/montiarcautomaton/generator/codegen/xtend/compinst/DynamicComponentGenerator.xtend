package de.montiarcautomaton.generator.codegen.xtend.compinst

import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.codegen.xtend.util.Ports
import de.montiarcautomaton.generator.codegen.xtend.util.Update
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import java.util.List
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference
import java.util.ArrayList
import de.montiarcautomaton.generator.codegen.xtend.compinst.Reconfigure
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicPorts
import de.montiarcautomaton.generator.codegen.xtend.compinst.CompInst
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicSubcomponents
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicSetup
import de.montiarcautomaton.generator.codegen.xtend.compinst.DynamicInit

class DynamicComponentGenerator extends de.montiarcautomaton.generator.codegen.xtend.ComponentGenerator {
	override generate(ComponentSymbol comp){
		
    generics = Utils.printFormalTypeParameters(comp)
    helper = new ComponentHelper(comp);

    return '''
			package «comp.packageName»;
			
			
			«Utils.printImports(comp)»
			import «comp.packageName».«comp.name»Input;
			import «comp.packageName».«comp.name»Result;
			import de.montiarcautomaton.runtimes.componentinstantiation.IDynamicComponent;
			import de.montiarcautomaton.runtimes.componentinstantiation.InterfaceChecker;
			import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
			import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
			import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
			import de.montiarcautomaton.runtimes.timesync.delegation.Port;
			import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
			import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
			import de.montiarcautomaton.runtimes.Log;
			import java.util.*;
			
			 
			 public class Dynamic«comp.name»«generics»      
			   «IF comp.superComponent.present» extends «comp.superComponent.get.fullName» 
			   	    «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
			   	    	«scTypeParams»«ENDFOR»>
			   	    «ENDIF»
			   «ENDIF»
			   implements IDynamicComponent {
			     
			   //ports
			   «Ports.print(comp.ports)»
			   
			   // component variables
			   «Utils.printVariables(comp)»
			   private String instanceName = null;
			   private String storeDir = null;
			   private String targetDir = null;
			   private LoaderManager loman;
			   private ILoader loader;
			   
			   
			   // config parameters
			   «Utils.printConfigParameters(comp)»
			 
			   «IF comp.isDecomposed»
			   	// subcomponents
			   	«DynamicSubcomponents.print(comp)»
			   	«printComputeComposed(comp)»
			   	
			   «ELSE»
			   	// the components behavior implementation
			   	private final IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> «Identifier.behaviorImplName»;
			   	
			   	«printComputeAtomic(comp)»
			   	private void initialize() {
			   	  // get initial values from behavior implementation
			   	  final «comp.name»Result«generics» result = «Identifier.behaviorImplName».getInitialValues();
			   	  
			   	  // set results to ports
			   	  setResult(result);
			   	  this.update();
			   	}
			   	private void setResult(«comp.name»Result«generics» result) {
			   	  «FOR portOut : comp.outgoingPorts»
			   	  	this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
			   	  «ENDFOR»
			   	}
			   «ENDIF»
			   
			   «DynamicSetup.print(comp)»
			   
			   «DynamicInit.print(comp)»
			   
			   «Update.print(comp)»
			   
			   «Reconfigure.print(comp)»
			   
			   «DynamicPorts.print(comp)»
			   
			   «CompInst.printCheckForCmp(comp)»
			   
			   «CompInst.printPropagatePortChanges(comp)»
			   
			   «CompInst.printSetLoaderConfiguration(comp)»
			   
			   «CompInst.printGetInstanceName(comp)»
			   
			   «CompInst.printGetInterface(comp)»
			   
			   
			   
			   «printConstructor(comp)»
			   
			   }
			   
		'''
  
	}
	
	  override printConstructor(ComponentSymbol comp) {
    return '''
			public Dynamic«comp.name»(«Utils.printConfiurationParametersAsList(comp)») {
			  «IF comp.superComponent.present»
			  	super(«FOR inhParam : getInheritedParams(comp) SEPARATOR ','» «inhParam» «ENDFOR»);
			  «ENDIF»
			  
			  «IF comp.isAtomic»
			  	«Identifier.behaviorImplName» = new «comp.name»Impl«generics»(
			  	«IF comp.hasConfigParameters»
			  		«FOR param : comp.configParameters SEPARATOR ','»
			  			«param.name»
			  		«ENDFOR»
			  	«ENDIF»);
			  «ENDIF»
			  // config parameters       
			  «FOR param : comp.configParameters»
			  	this.«param.name» = «param.name»;
			  «ENDFOR»
			}
		'''
  }
  
    def private static List<String> getInheritedParams(ComponentSymbol component) {
    var List<String> result = new ArrayList;
    var List<JFieldSymbol> configParameters = component.getConfigParameters();
    if (component.getSuperComponent().isPresent()) {
      var ComponentSymbolReference superCompReference = component.getSuperComponent().get();
      var List<JFieldSymbol> superConfigParams = superCompReference.getReferencedSymbol().getConfigParameters();
      if (!configParameters.isEmpty()) {
        for (var i = 0; i < superConfigParams.size(); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
  
    override printComputeComposed(ComponentSymbol comp) {
    return '''
			@Override
			public void compute() {
			// trigger computation in all subcomponent instances
			  «FOR subcomponent : comp.subComponents»
			  	this.«subcomponent.name».compute();
			  	if (new«subcomponent.name» != null) {
			  		new«subcomponent.name».compute();
			  		loader.deleteFile(«subcomponent.name».getInstanceName());
			  	}
			  «ENDFOR»
			}
		'''
  }
}
