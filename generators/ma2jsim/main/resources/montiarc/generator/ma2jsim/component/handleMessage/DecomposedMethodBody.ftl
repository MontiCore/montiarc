<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    if(receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}().getQualifiedName()))
      handlePortForward((montiarc.rte.port.TimeAwarePortForward<?>) ${prefixes.port()}${inPort.getName()}());
    <#sep> else </#sep>
</#list>
<#list ast.getSymbol().getAllOutgoingPorts() as outPort>
    if(receivingPort.getQualifiedName().equals(${prefixes.port()}${outPort.getName()}().getQualifiedName()))
      ((montiarc.rte.port.TimeAwarePortForward${"<?>"}) ${prefixes.port()}${outPort.getName()}()).forward();
    <#sep> else </#sep>
</#list>