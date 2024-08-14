<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>


<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>

<#list helper.getTransitionsForPortEvents(modeAutomaton) as port, transitions>
  <#assign portName = port.getName()>
  public void ${prefixes.message()}${portName}(<@Util.getTypeString port.getType()/> ${portName}) {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}

    switch (currentMode) {
      <#list modes as mode>
        <#assign takableTransitions = helper.filterTransitionsForSourceMode(transitions, mode.getName())>

        case ${mode.getName()}:
          <@ModeUtil.transitioningBehavior takableTransitions/>
          break;
      </#list>
      default: throw new IllegalStateException("Unknown current mode: " + currentMode.name);
    }
  }

</#list>

<#-- Methods for input ports that do not trigger any behavior. Such methods have not been create yet,
  -- but are a required part of the automaton API.
  -->
<#list helper.getInPortsNotTriggeringAnyTransition(modeAutomaton, ast) as port>
  <#assign handleMsgOnPort>${prefixes.message()}${port.getName()}</#assign>

  public void ${handleMsgOnPort}(<@Util.getTypeString port.getType()/> ${port.getName()}) { }
</#list>
