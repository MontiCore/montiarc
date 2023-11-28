<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign ma=helper.getModeAutomaton(ast).get()/>
<#assign isEvent=helper.isEventBased(ma)/>
protected void <@MethodNames.handleModeAutomaton/>(montiarc.rte.port.IInPort<?> receivingPort) {
    <#if isEvent>
        <#list ast.getSymbol().getAllIncomingPorts() as inPort>
            if(receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}().getQualifiedName())) {
              if (${prefixes.port()}${inPort.getName()}().isBufferEmpty() || ${prefixes.port()}${inPort.getName()}().isTickBlocked()) return;
              else <@MethodNames.getModeAutomaton/>().${prefixes.message()}${portSym.getName()}();
            }
            <#sep> else </#sep>
        </#list>
    <#else>
        if(getAllInPorts().stream()
          .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
          .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
          .anyMatch(montiarc.rte.port.AbstractInPort::isBufferEmpty)) { return; }

        if (<@MethodNames.getModeAutomaton/>().canExecuteTransition()) {
          <@MethodNames.getModeAutomaton/>().executeAnyValidTransition();
        }
    </#if>
}
