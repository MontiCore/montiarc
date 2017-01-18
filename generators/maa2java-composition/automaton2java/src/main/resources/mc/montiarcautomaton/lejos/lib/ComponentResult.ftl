${tc.params("de.montiarcautomaton.lejosgenerator.helper.ComponentHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
"String name", "String resultName", 
"java.util.Collection<de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol> portsOut")}
package ${_package};

<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>
import de.montiarcautomaton.runtimes.timesync.implementation.IResult;

public class ${resultName} implements IResult { 
  // variables  
  <#list portsOut as port>
  private ${helper.getPortTypeName(port)} ${port.getName()};
  </#list>
  
  public ${resultName}() {}
  
  <#if portsOut?size != 0>
  public ${resultName}(<#list portsOut as port>${helper.getPortTypeName(port)} ${port.getName()}<#sep>, </#list>) {
    <#list portsOut as port>
    this.${port.getName()} = ${port.getName()};
    </#list>
  }
  </#if>

  // getter
  <#list portsOut as port>
  public ${helper.getPortTypeName(port)} get${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  
  </#list>
  
  // setter
  <#list portsOut as port>
  public void set${port.getName()?cap_first}(${helper.getPortTypeName(port)} ${port.getName()}) {
  	this.${port.getName()} = ${port.getName()};
  }
  
  </#list>
  
  @Override
  public String toString() {
  	String result = "[";
  	<#list portsOut as port>
  	result += "${port.getName()}: " + this.${port.getName()} + " ";  	
  	</#list>
  	return result + "]";
  } 
}
