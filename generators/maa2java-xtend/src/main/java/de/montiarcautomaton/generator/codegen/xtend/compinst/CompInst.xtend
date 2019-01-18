package de.montiarcautomaton.generator.codegen.xtend.compinst

import montiarc._symboltable.ComponentSymbol

class CompInst {
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
	
	
		def static printPropagatePortChanges(ComponentSymbol comp){
		return
		'''
		@Override
		  public void propagatePortChanges(List<Port> changedPorts) {
		    }
		
		'''
	}
	
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
	
	}