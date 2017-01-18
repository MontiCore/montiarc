${tc.params("de.montiarcautomaton.generator.helper.ComponentHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports", "String name",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsIn", "java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsOut",
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol> subComponents", "java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol> connectors")}
package ${_package};

<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;

public class ${name} implements IComponent {
  // port fields
  <#list portsIn as port>
  private Port<${helper.getPortTypeName(port)}> ${port.getName()};
  </#list>
  <#list portsOut as port>
  private Port<${helper.getPortTypeName(port)}> ${port.getName()};
  </#list>
  
  // port setter
  <#list portsIn as port>
  public void setPort${port.getName()?cap_first} (Port<${helper.getPortTypeName(port)}> port) {
  	this.${port.getName()} = port;
  }
  
  </#list>
  <#list portsOut as port>
  public void setPort${port.getName()?cap_first} (Port<${helper.getPortTypeName(port)}> port) {
  	this.${port.getName()} = port;
  }
  
  </#list>
  // port getter
  <#list portsIn as port>
  public Port<${helper.getPortTypeName(port)}> getPort${port.getName()?cap_first} () {
  	return this.${port.getName()};
  }
  
  </#list>
  <#list portsOut as port>
  public Port<${helper.getPortTypeName(port)}> getPort${port.getName()?cap_first} () {
  	return this.${port.getName()};
  }
  
  </#list>
  
  // subcomponents
  <#list subComponents as component>
  private final ${helper.getSubComponentTypeName(component)} ${component.getName()}; 
  </#list>

  // subcomponent getter
  <#list subComponents as component>
  public ${helper.getSubComponentTypeName(component)} getComponent${component.getName()?cap_first}() {
  	return this.${component.getName()};
  }
  
  </#list>
  
  public ${name}() {
    // instantiate all subcomponents
  	<#list subComponents as component>
  	this.${component.getName()} = new ${helper.getSubComponentTypeName(component)}(<#list helper.getParamValues(component) as param>${param}<#sep>, </#list>); 
  	</#list>
  	
  	// set up output ports
  	<#list portsOut as port>
  	this.${port.getName()} = new Port<${helper.getPortTypeName(port)}>();
  	</#list>
  }

  @Override
  public void init() {
  	// set up unused input ports
  	<#list portsIn as port>
  	if (this.${port.getName()} == null) {this.${port.getName()} = Port.EMPTY;}
  	</#list>
  	
  	// set up connectors
  	<#list connectors as conn>
  	${helper.getConnectorComponentName(conn, false)}.setPort${helper.getConnectorPortName(conn, false)?cap_first}(${helper.getConnectorComponentName(conn, true)}.getPort${helper.getConnectorPortName(conn, true)?cap_first}());
  	</#list>
  	
  	// init all subcomponents
  	<#list subComponents as component>
  	this.${component.getName()}.init(); 
  	</#list>
  }

  @Override
  public void compute() {
  	// trigger computation in all subcomponent instances
  	<#list subComponents as component>
  	this.${component.getName()}.compute(); 
  	</#list>
  }

  @Override
  public void update() {
  	// update subcomponent instances
  	<#list subComponents as component>
  	this.${component.getName()}.update(); 
  	</#list>
  }
  
}
