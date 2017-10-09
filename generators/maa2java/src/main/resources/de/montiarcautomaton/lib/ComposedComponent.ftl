${tc.params("de.montiarcautomaton.generator.helper.ComponentHelper helper", "montiarc._symboltable.ComponentSymbol compSym", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports", "String name",
"java.util.Collection<montiarc._symboltable.PortSymbol> portsIn", "java.util.Collection<montiarc._symboltable.PortSymbol> portsOut",
"java.util.Collection<montiarc._symboltable.ComponentInstanceSymbol> subComponents", "java.util.Collection<montiarc._symboltable.ConnectorSymbol> connectors")}
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
  private ${helper.getSubComponentTypeName(component)} ${component.getName()}; 
  </#list>

  // subcomponent getter
  <#list subComponents as component>
  public ${helper.getSubComponentTypeName(component)} getComponent${component.getName()?cap_first}() {
  	return this.${component.getName()};
  }
  
  </#list>
  
  public ${name}() {
  }
  
  @Override
  public void setUp() {
     // instantiate all subcomponents
    <#list subComponents as component>
    this.${component.getName()} = new ${helper.getSubComponentTypeName(component)}(<#list helper.getParamValues(component) as param>${param}<#sep>, </#list>); 
    </#list>
    
    //set up all sub components  
    <#list subComponents as component>    
    this.${component.getName()}.setUp();
    </#list>
    
    // set up output ports
    <#list portsOut as port>
    this.${port.getName()} = new Port<${helper.getPortTypeName(port)}>();
    </#list>
    
    // propagate children's output ports to own output ports
    <#list connectors as conn>
      <#if !helper.isIncomingPort(compSym,conn, false, conn.getTarget())>
        ${helper.getConnectorComponentName(conn, false)}.setPort${helper.getConnectorPortName(conn, false)?cap_first}(${helper.getConnectorComponentName(conn, true)}.getPort${helper.getConnectorPortName(conn, true)?cap_first}());
      </#if>
    </#list>
    
  }
  
  

  @Override
  public void init() {
  	// set up unused input ports
  	<#list portsIn as port>
  	if (this.${port.getName()} == null) {this.${port.getName()} = Port.EMPTY;}
  	</#list>
  	
  	// connect outputs of children with inputs of children, by giving 
  	// the inputs a reference to the sending ports 
  	<#list connectors as conn>
  	  <#if helper.isIncomingPort(compSym,conn, false, conn.getTarget())>
  	    ${helper.getConnectorComponentName(conn, false)}.setPort${helper.getConnectorPortName(conn, false)?cap_first}(${helper.getConnectorComponentName(conn, true)}.getPort${helper.getConnectorPortName(conn, true)?cap_first}());
  	  </#if>
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
