${tc.signature("helper", "_package", "imports","name", "inputName", "portsIn")}


package ${_package};

<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>
import de.montiarcautomaton.runtimes.timesync.implementation.IInput;

public class ${inputName}<#if helper.isGeneric()><<#list helper.getGenericTypeParametersWithInterfaces() as param>${param}<#sep>,</#list>></#if><#if helper.hasSuperComp()> extends ${helper.getSuperComponentFqn()}Input<#if helper.isSuperComponentGeneric()><<#list helper.getSuperCompActualTypeArguments() as typeArg>${typeArg}<#sep>, </#sep></#list>></#if></#if> implements IInput {
  // variables  
  <#list portsIn as port>
  private ${helper.getRealPortTypeString(port)} ${port.getName()};
  </#list>
  
  public ${inputName}() {<#if helper.hasSuperComp()>
    super();
  </#if>}
  
  <#if helper.getAllInPorts()?size != 0>
  public ${inputName}(<#list helper.getAllInPorts() as port>${helper.getRealPortTypeString(port)} ${port.getName()}<#sep>, </#list>) {
    <#if helper.hasSuperComp()>
    super(<#list helper.getSuperInPorts() as port>${port.getName()}<#sep>, </#list>);
    </#if>
    <#list portsIn as port>
    this.${port.getName()} = ${port.getName()};
    </#list>
  }
  </#if>

  // getter
  <#list portsIn as port>
  public ${helper.getRealPortTypeString(port)} get${port.getName()?cap_first}() {
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
