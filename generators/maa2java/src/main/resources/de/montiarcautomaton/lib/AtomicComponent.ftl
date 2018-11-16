${tc.signature("helper",	"_package",	"imports", "name",	"resultName",	"inputName", 
	"implName", "variables", "portsIn", "portsOut", "allPortsOut", "configParams")}
	
package ${_package};

import ${_package}.${inputName};
import ${_package}.${resultName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.runtimes.Log;

public class ${name}<#if helper.isGeneric()><<#list helper.getGenericTypeParametersWithInterfaces() as param>${param}<#sep>,</#list>></#if><#if helper.hasSuperComp()> extends ${helper.getSuperComponentFqn()}<#if helper.isSuperComponentGeneric()><<#list helper.getSuperCompActualTypeArguments() as typeArg>${typeArg}<#sep>, </#sep></#list>></#if></#if> implements IComponent {
  
  // component variables
  <#list variables as var>
  private ${helper.printVariableTypeName(var)} ${var.getName()};
  </#list>
  
  // config parameters
  <#list configParams as param>
  private final ${helper.printParamTypeName(param)} ${param.getName()};
  </#list>
  
  // port fields
  <#list portsIn as port>
  protected Port<${helper.printPortType(port)}> ${port.getName()};
  </#list>
  
  <#list portsOut as port>
  protected Port<${helper.printPortType(port)}> ${port.getName()};
  </#list>
  
  // port setter
  <#list portsIn as port>
  public void setPort${port.getName()?cap_first}(Port<${helper.getRealPortTypeString(port)}> port) {
  	this.${port.getName()} = port;
  }

  public Port<${helper.getRealPortTypeString(port)}> getPort${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  
  </#list>
  // port getter
  <#list portsOut as port>
  public void setPort${port.getName()?cap_first}(Port<${helper.getRealPortTypeString(port)}> port) {
  	this.${port.getName()} = port;
  }
  public Port<${helper.getRealPortTypeString(port)}> getPort${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  </#list>

  <#if helper.containsIdentifier("behaviorImpl")>
    <#assign implVarName = "r__behaviorImpl">
  <#else>
    <#assign implVarName = "behaviorImpl">
  </#if>

  // the components behavior implementation
  private final IComputable<${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>, ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>> ${implVarName};

  public ${name}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    <#if helper.hasSuperComp()>super(<#list helper.getInheritedParams() as inhParam>${inhParam}<#sep>, </#sep></#list>);</#if>
    ${implVarName} = new ${implName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>(<#list configParams as param>${param.getName()}<#sep>, </#list>);
    // config parameters
  <#list configParams as param>
    this.${param.getName()} = ${param.getName()};
  </#list>
  }
  
  @Override
  public void setUp() {
    <#if helper.hasSuperComp()>super.setUp();</#if>
    // set up output ports
    <#list portsOut as port>
    this.${port.getName()} = new Port<${helper.getRealPortTypeString(port)}>();
    </#list>
    
    this.initialize();
  }

  @Override
  public void init() {
    <#if helper.hasSuperComp()>super.init();</#if>  
  	// set up unused input ports
  	<#list portsIn as port>
  	if (this.${port.getName()} == null) {this.${port.getName()} = Port.EMPTY;}
  	</#list>
  }
  
  private void setResult(${resultName}<#if helper.isGeneric()> < <#list helper.getGenericParameters() as param>${param}<#sep>,</#list> > </#if> result) {
  	<#list allPortsOut as port>
  	this.getPort${port.getName()?cap_first}().setNextValue(result.get${port.getName()?cap_first}());
  	</#list>
  }

  <#-- <#if behaviorEmbedding.isPresent()> -->
  @Override
  public void compute() {
    // collect current input port values
    final ${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> input = new ${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>(<#list helper.getAllInPorts() as port>this.getPort${port.getName()?cap_first}().getCurrentValue()<#sep>, </#list>);
    //Logger.log("${name}", "compute(" + input.toString() + ")");
    
    try {
      // perform calculations
      final ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> result = ${implVarName}.compute(input);
      
      // set results to ports
      setResult(result);
    } catch (Exception e) {
      Log.error("${name}", e);
    }
  }
  <#-- </#if> -->

  @Override
  public void update() {
    <#if helper.hasSuperComp()>super.update();</#if>
    // update computed value for next computation cycle in all outgoing ports
  	<#list portsOut as port>
  	this.${port.getName()}.update();
  	</#list>
  }
  
  private void initialize() {
     // get initial values from behavior implementation
    final ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> result = ${implVarName}.getInitialValues();
    
    // set results to ports
    setResult(result);
  }
  
}
