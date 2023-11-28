<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign atomic = ast.getSymbol().isAtomic()/>
protected void <@MethodNames.portSetup/>() {
<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign dynamicType><#if atomic>
        <@Util.getStaticPortClass inPort atomic/>
    <#else>
        montiarc.rte.port.TimeAwarePortForward
    </#if></#assign>
  this.${prefixes.port()}${inPort.getName()} = new ${dynamicType}<>(getName() + ".${inPort.getName()}", this);
</#list>

<#list ast.getSymbol().getAllOutgoingPorts() as outPort>
    <#assign dynamicType><#if !atomic>
        montiarc.rte.port.TimeAwarePortForward
    <#else>
        <@Util.getStaticPortClass outPort atomic/>
    </#if></#assign>
  this.${prefixes.port()}${outPort.getName()} = new ${dynamicType}<>(getName() + ".${outPort.getName()}", this);
</#list>
}