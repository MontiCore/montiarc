<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
@Override
public void handleMessage(montiarc.rte.port.IInPort<?> receivingPort) {
<#if helper.getModeAutomaton(ast).isPresent()><@MethodNames.handleModeAutomaton/>(receivingPort);</#if>
<#if ast.getSymbol().isAtomic()>
    <#list ast.getSymbol().getAllIncomingPorts() as inPort>
        if(receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}().getQualifiedName())) {
          if (${prefixes.port()}${inPort.getName()}().isBufferEmpty()) return;
          else <@MethodNames.handleBufferImplementation inPort/>();
        }
        <#sep> else </#sep>
    </#list>
</#if>
}
