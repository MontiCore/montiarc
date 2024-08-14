<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign syncMsgClass>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign inPorts = ast.getSymbol().getAllIncomingPorts()>

public void tick(${syncMsgClass} msg) {
  this.tick( <#list inPorts as inPort> msg.${inPort.getName()} <#sep>,</#list> );
}

public void tick(
  <#list inPorts as inPort>
  <@Util.getTypeString inPort.getType()/> ${inPort.getName()}
  <#sep>,</#list>
) {
${tc.include("montiarc.generator.ma2jsim.component.modes.TickBehaviorBody.ftl")}
}
