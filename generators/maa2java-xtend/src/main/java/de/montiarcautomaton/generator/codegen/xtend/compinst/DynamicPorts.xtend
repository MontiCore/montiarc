package de.montiarcautomaton.generator.codegen.xtend.compinst

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

class DynamicPorts {
	
	/**
	 * Methods for getting ports without nowing the exact interface of the component.
	 */
	def static print(ComponentSymbol comp){
		var ComponentHelper helper = new ComponentHelper(comp);
		return
		'''
		@Override
		public <T> void setPort(String name, Port<T> port) {
			«FOR inPort : comp.ports»
      	if (name.equals("«inPort.name»")){
      		setPort«inPort.name.toFirstUpper»((Port<«helper.getRealPortTypeString(inPort)»>) port);
      	}
			      	«ENDFOR»
			
		}
		@Override
		public <T> Port<T> getPort(String name) {
			«FOR outPort : comp.outgoingPorts»
      	if (name.equals("«outPort.name»")){
      		return (Port<T>) getPort«outPort.name.toFirstUpper»();
      	}
			      	«ENDFOR»
			return null;     	
		}
		
		
		@Override
		public List<Port> getPorts(){
			List<Port> ports = new ArrayList<>();
			«FOR inPort : comp.outgoingPorts»
			ports.add(getPort«inPort.name.toFirstUpper»());
			«ENDFOR»
			return ports;
		}
		'''
	}
}