<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>

<#assign syncMsgClass>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign syncedInPorts = helper.getSyncedInPortsOf(ast.getSymbol())>
<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>

public void tick(${syncMsgClass} msg) {
  this.doTick( <#list syncedInPorts as inPort> msg.${inPort.getName()} <#sep>,</#list> );
}

protected void doTick(
  <#list syncedInPorts as inPort>
  <@Util.getTypeString inPort.getType()/> ${inPort.getName()}
  <#sep>,</#list>
) {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}

  switch (currentMode) {
    <#list modes as mode>
      <#assign transitions = helper.getTransitionsForTickEventFromState(modeAutomaton, mode)>

      case ${mode.getName()}:
        <@ModeUtil.transitioningBehavior transitions/>
        break;
    </#list>
    default:
      throw new IllegalStateException("Unknown current mode: " + currentMode.name);
  }
}
