<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    if(receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}().getQualifiedName())) {
      if (${prefixes.port()}${inPort.getName()}().isBufferEmpty()) return;
      else <@MethodNames.handleBufferImplementation inPort/>();
    }
    <#sep> else </#sep>
</#list>
