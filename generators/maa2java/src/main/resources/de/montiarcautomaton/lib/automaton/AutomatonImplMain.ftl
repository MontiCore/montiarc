${tc.signature("helper", "_package", "imports",	"name",	"resultName",	"inputName", 
	"implName", "portsIn", "compHelper", "variables",	"states", "configParams")}
	
package ${_package};

import ${_package}.${resultName};
import ${_package}.${inputName};
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

public class ${implName}<#if helper.isGeneric()> < <#list helper.getGenericParameters() as param>${param}<#sep>,</#list> > </#if> implements IComputable<${inputName}, ${resultName}> {
  private static enum ${name}State {
    <#list states><#items as state>${state.getName()}<#sep>, </#sep></#items>;</#list>
  }

  <#-- Determine the correct names for variables in case of naming conflicts. -->
  <#if helper.containsIdentifier("currentState")>
    <#assign currentStateName = "r__currentState">
  <#else>
    <#assign currentStateName = "currentState">
  </#if>
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

  // holds the current state of the automaton
  private ${name}State ${currentStateName};
  
  // variables
  <#list variables as variable>
  private ${compHelper.printVariableTypeName(variable)} ${variable.getName()};
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

  @Override
  public ${resultName} getInitialValues() {
    final ${resultName} ${resultVarName} = new ${resultName}();
    

    // initial reaction
    <#list helper.getInitialReaction(helper.getInitialState()) as assignment>
    <#if assignment.isAssignment()>
      <#if helper.isPort(assignment.getLeft())>
        ${resultVarName}.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
      <#else>
        ${assignment.getLeft()} = ${assignment.getRight()};
      </#if>
    <#else>
      ${assignment.getRight()};
    </#if>
    
    </#list>
    
    // initial state
    ${currentStateName} = ${name}State.${helper.getInitialState().getName()};
    
    return ${resultVarName};
  }

  @Override
  public ${resultName} compute(${inputName} ${inputVarName}) {
    // inputs
    <#list portsIn as port>
  	final ${helper.getRealPortTypeString(port)} ${port.getName()} = ${inputVarName}.get${port.getName()?cap_first}();
  	</#list>
  
    final ${resultName} ${resultVarName} = new ${resultName}();
    
    // first current state to reduce stimuli and guard checks
    switch (${currentStateName}) {
    <#list states as state>
      case ${state.getName()}:
      <#list helper.getTransitions(state) as transition>
        // transition: ${transition.toString()}
        if (${helper.getGuard(transition)!"true"}
        && true) {


          // reaction
          <#list helper.getReaction(transition) as assignment>
            <#if assignment.isAssignment()>
              <#if assignment.isVariable(assignment.getLeft())>
                ${assignment.getLeft()} = ${assignment.getRight()};
              <#else>
                ${resultVarName}.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
              </#if>
            <#else>
              ${assignment.getRight()};
            </#if>
          </#list>
          
          //Log.log("${implName}", "${transition.toString()}");
             
          // state change
          ${currentStateName} = ${name}State.${transition.getTarget().getName()};
          break;
        }
      </#list>  
        break;
    </#list>
    }
    
    return ${resultVarName};
  }
  
}
