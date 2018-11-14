${tc.signature("helper", "_package", "imports", "name", "resultName", "portsOut")}

package ${_package};

<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>
import de.montiarcautomaton.runtimes.timesync.implementation.IResult;

public class ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if><#if helper.hasSuperComp()> extends ${helper.getSuperComponentFqn()}Result<#if helper.superCompGeneric()><<#list helper.getSuperCompActualTypeArguments() as typeArg>${typeArg}<#sep>, </#sep></#list>></#if></#if> implements IResult {
  // variables  
  <#list portsOut as port>
  private ${helper.getRealPortTypeString(port)} ${port.getName()};
  </#list>
  
  public ${resultName}() {<#if helper.hasSuperComp()>
    super();
  </#if>}
  
  <#if helper.getAllOutPorts()?size != 0>
  public ${resultName}(<#list helper.getAllOutPorts() as port>${helper.getRealPortTypeString(port)} ${port.getName()}<#sep>, </#list>) {
    <#if helper.hasSuperComp()>super(<#list helper.getSuperOutPorts() as port>${port.getName()}<#sep>, </#list>);</#if>
    <#list portsOut as port>
    this.${port.getName()} = ${port.getName()};
    </#list>
  }
  </#if>

  // getter
  <#list portsOut as port>
  public ${helper.getRealPortTypeString(port)} get${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  
  </#list>
  
  // setter
  <#list portsOut as port>
  public void set${port.getName()?cap_first}(${helper.getRealPortTypeString(port)} ${port.getName()}) {
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
