${tc.params("de.montiarcautomaton.generator.helper.ComponentHelper helper", "String _package", "java.util.Collection<de.monticore.symboltable.ImportStatement> imports",
"String name", "String inputName",
"java.util.Collection<montiarc._symboltable.PortSymbol> portsIn")}
package ${_package};

<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>
import de.montiarcautomaton.runtimes.timesync.implementation.IInput;

public class ${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> implements IInput {
  // variables  
  <#list portsIn as port>
  private ${helper.getPortTypeName(port)} ${port.getName()};
  </#list>
  
  public ${inputName}() {}
  
  <#if portsIn?size != 0>
  public ${inputName}(<#list portsIn as port>${helper.getPortTypeName(port)} ${port.getName()}<#sep>, </#list>) {
    <#list portsIn as port>
    this.${port.getName()} = ${port.getName()};
    </#list>
  }
  </#if>

  // getter
  <#list portsIn as port>
  public ${helper.getPortTypeName(port)} get${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  
  </#list>
  
  @Override
  public String toString() {
  	String result = "[";
  	<#list portsIn as port>
  	result += "${port.getName()}: " + this.${port.getName()} + " ";  	
  	</#list>
  	return result + "]";
  }
}
