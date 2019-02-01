package de.montiarcautomaton.generator.codegen.xtend.compinst

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

class CompInst {
	
	/**
	 * checkForComponent() gets new subcomponents from the Loader, and sets them up for replacement.
	 */
		def static printCheckForCmp(ComponentSymbol comp){
		return
		'''
		@Override
		public void checkForCmp() {
	    «FOR subcomponent : comp.subComponents»
			
		  Class new«subcomponent.name»Class = null;
		  Optional<Object> «subcomponent.name»Opt = loader.hasNewSubComponent(«subcomponent.name».getInstanceName());
		  if («subcomponent.name»Opt.isPresent()){
			  new«subcomponent.name»Class = (Class) «subcomponent.name»Opt.get();
		  }
		  
		  if (new«subcomponent.name»Class != null) {
			  try {
				  System.out.println("New «subcomponent.name» found");
				  new«subcomponent.name» = (IDynamicComponent) new«subcomponent.name»Class.newInstance();
				  new«subcomponent.name».init();
				  new«subcomponent.name».setUp();
			  } catch (InstantiationException | IllegalAccessException e) {
			        e.printStackTrace();
		      }
			
				  }
		«ENDFOR»
		}
		'''
	}
	
	
	/**
	 * Ports that have changed during a subcomponent exchange need to be
	 * set anew. This needs to happen both bottom-up and top-down. Fort this
	 * this method gives a list of all changed ports to subcomponents.
	 */
		def static printPropagatePortChanges(ComponentSymbol comp){
		var ComponentHelper helper = new ComponentHelper(comp);
			
		return
		'''
		@Override
		  public void propagatePortChanges(List<Port> changedPorts) {
		  	«FOR connector : comp.connectors»
		  	if (changedPorts.contains(«helper.getConnectorComponentName(connector,true)».getPort("«helper.getConnectorPortName(connector, true)»"))){
		  		«helper.getConnectorComponentName(connector, false)».setPort("«helper.getConnectorPortName(connector, false)»",«helper.getConnectorComponentName(connector,true)».getPort("«helper.getConnectorPortName(connector, true)»"));
		  	}	
		  	«ENDFOR» 
		  	
		  	«FOR subcomponent : comp.subComponents»
		  	this.«subcomponent.name».propagatePortChanges(changedPorts);
		  				«ENDFOR»
		    }
		
		'''
	}
	
	/**
	 * Sets path variables for the Loader
	 */
		def static printSetLoaderConfiguration(ComponentSymbol comp){
		return
		'''
		@Override
		  public void setLoaderConfiguration(String instanceName, String storeDir, String targetDir, LoaderManager loaderManager) {
		    this.instanceName = instanceName;
		    this.storeDir = storeDir;
		    this.targetDir = targetDir;
		    this.loman = loaderManager;
		    
		    «FOR subcomponent : comp.subComponents»
		    this.«subcomponent.name».setLoaderConfiguration( instanceName + ".«subcomponent.name»",  storeDir,  targetDir,  loaderManager);
		    «ENDFOR»
		  }
		
		'''
	}
	
	def static printGetInstanceName(ComponentSymbol comp){
		return
		'''
		@Override
		public String getInstanceName() {
		    return instanceName;
		}
		
		'''
	}
	
	/**
	 * Returns interface of the component. Used for checking if a new subcomponent can actually
	 * replace an old one.
	 */
	def static printGetInterface(ComponentSymbol comp){
		var ComponentHelper helper = new ComponentHelper(comp);
		
		return
		'''
		@Override
		public List<String> getInterface() {
			List<String> compInterface = new ArrayList<>();
			«FOR inPort : comp.incomingPorts»
			compInterface.add("In-«inPort.name»-«helper.getRealPortTypeString(inPort)»");
			«ENDFOR»
			«FOR inPort : comp.outgoingPorts»
			compInterface.add("Out-«inPort.name»-«helper.getRealPortTypeString(inPort)»");
			«ENDFOR»
			«IF comp.superComponent.present»
			compInterface.add("Supercomponent-«comp.superComponent.get.fullName»"); 
			
			«ENDIF»
			
		    return compInterface;
		}
		
		'''
	}
	
	}