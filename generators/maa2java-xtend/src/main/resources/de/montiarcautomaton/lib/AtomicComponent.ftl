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

public class ${name}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if><#if helper.hasSuperComp()> extends ${helper.getSuperComponentFqn()}</#if> implements IComponent {
  
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
  protected Port<${helper.printPortTypeName(port)}> ${port.getName()};
  </#list>
  
  <#list portsOut as port>
  protected Port<${helper.printPortTypeName(port)}> ${port.getName()};
  </#list>
  
  // port setter
  <#list portsIn as port>
  public void setPort${port.getName()?cap_first}(Port<${helper.getPortTypeName(port)}> port) {
  	this.${port.getName()} = port;
  }
  
  </#list>
  // port getter
  <#list portsOut as port>
  public Port<${helper.getPortTypeName(port)}> getPort${port.getName()?cap_first}() {
  	return this.${port.getName()};
  }
  </#list>
  
  // the components behavior implementation
  private final IComputable<${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>, ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>> behaviorImpl;
  
  public ${name}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    behaviorImpl = new ${implName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>(<#list configParams as param>${param.getName()}<#sep>, </#list>);
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
    this.${port.getName()} = new Port<${helper.getPortTypeName(port)}>();
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
    final ${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> input = new ${inputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>(<#list helper.getAllInPorts() as port>this.${port.getName()}.getCurrentValue()<#sep>, </#list>);
    //Logger.log("${name}", "compute(" + input.toString() + ")");
    
    try {
      // perform calculations
      final ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> result = behaviorImpl.compute(input);
      
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
    final ${resultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> result = behaviorImpl.getInitialValues();
    
    // set results to ports
    setResult(result);
  }
  
}
