${tc.signature("helper", "_package", "imports", "name", "resultName", "inputName", "implName",
"portsIn", "portsOut", "configParams", "compVariables", "ajava", "initializations")}


package ${_package};

import ${_package}.${resultName};
import ${_package}.${inputName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

public class ${implName} implements IComputable<${inputName}, ${resultName}> {
  
  //component variables
  <#list compVariables as compVariable>
    private ${helper.printVariableTypeName(compVariable)} ${compVariable.getName()};
  </#list>
  
  // config parameters
  <#list configParams as param>
  private final ${helper.printParamTypeName(param)} ${param.getName()};
  </#list>
  
  public ${implName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
    <#list configParams as param>
    this.${param.getName()} = ${param.getName()};
    </#list>
  }

  <#if helper.containsIdentifier("result")>
    <#assign resultVarName = "r__result">
  <#else>
    <#assign resultVarName = "result">
  </#if>
  <#if helper.containsIdentifier("input")>
    <#assign inputVarName = "r__input">
  <#else>
    <#assign inputVarName = "input">
  </#if>

  @Override
  <#-- The name of the result variable is changed, as the initialization with
    null of the outgoing ports might lead to issues if the name "result" shadows
    the name of a variable, port, etc-->
  public ${resultName} getInitialValues() {
    final ${resultName} ${resultVarName} = new ${resultName}();
    
    try {
    <#list portsOut as portOut>
    ${helper.getRealPortTypeString(portOut)} ${portOut.getName()} = null;
    </#list>
        
    <#list initializations as init>
      ${helper.printInit(init)}
    </#list>

    <#list portsOut as portOut>
    ${resultVarName}.set${portOut.getName()?cap_first}(${portOut.getName()});
    </#list>
    } catch(Exception e) {
      e.printStackTrace();
    }

    return ${resultVarName};
  }

  @Override
  <#-- The name of the input and result variables are changed, as otherwise
    syntactical and semantical errors with models using those names for ports,
    variables, or similar might occur. -->
  public ${resultName} compute(${inputName} ${inputVarName}) {
    // inputs
    <#list portsIn as portIn>
  	final ${helper.getRealPortTypeString(portIn)} ${portIn.getName()} = ${inputVarName}.get${portIn.getName()?cap_first}();
  	</#list>
  
    final ${resultName} ${resultVarName} = new ${resultName}();
    
    <#list portsOut as portOut>
    ${helper.getRealPortTypeString(portOut)} ${portOut.getName()} = ${resultVarName}.get${portOut.getName()?cap_first}();
    </#list>
    
    <#-- print java statements here -->
    ${ajava}
    
    <#-- always add all outgoing values to result -->
    <#list portsOut as portOut>
    ${resultVarName}.set${portOut.getName()?cap_first}(${portOut.getName()});
    </#list>
    
    
    return ${resultVarName};
  }
  
}
