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
  private static enum State {
    <#list states><#items as state>${state.getName()}<#sep>, </#sep></#items>;</#list>
  }
  
  // holds the current state of the automaton
  private State currentState;
  
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
    final ${resultName} result = new ${resultName}();
    

    // initial reaction
    <#list helper.getInitialReaction(helper.getInitialState()) as assignment>
    <#if assignment.isAssignment()>
      <#if helper.isPort(assignment.getLeft())>
        result.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
      <#else>
        ${assignment.getLeft()} = ${assignment.getRight()};
      </#if>
    <#else>
      ${assignment.getRight()};
    </#if>
    
    </#list>
    
    // initial state
    currentState = State.${helper.getInitialState().getName()};
    
    return result;
  }

  @Override
  public ${resultName} compute(${inputName} input) {
    // inputs
    <#list portsIn as port>
  	final ${helper.getPortTypeName(port)} ${port.getName()} = input.get${port.getName()?cap_first}();
  	</#list>
  
    final ${resultName} result = new ${resultName}();
    
    // first current state to reduce stimuli and guard checks
    switch (currentState) {
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
                result.set${assignment.getLeft()?cap_first}(${assignment.getRight()});
              </#if>
            <#else>
              ${assignment.getRight()};
            </#if>
          </#list>
          
          //Log.log("${implName}", "${transition.toString()}");
             
          // state change
          currentState = State.${transition.getTarget().getName()};
          break;
        }
      </#list>  
        break;
    </#list>
    }
    
    return result;
  }
  
}
