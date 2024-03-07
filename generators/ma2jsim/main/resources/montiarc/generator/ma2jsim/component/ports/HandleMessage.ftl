<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
@Override
public void handleMessage(montiarc.rte.port.IInPort<?> receivingPort) {
<#if helper.getModeAutomaton(ast).isPresent()><@MethodNames.handleModeAutomaton/>(receivingPort);</#if>
  if (this.isAtomic) {
    <#list ast.getSymbol().getAllIncomingPorts() as inPort>
        if(receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().getQualifiedName())) {
          if (${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().isBufferEmpty()) return;
          else <@MethodNames.handleBufferImplementation inPort/>${helper.portVariantSuffix(ast, inPort)}();
        }
        <#sep> else </#sep>
    </#list>
  }
}
