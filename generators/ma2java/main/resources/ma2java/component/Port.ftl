<#-- (c) https://github.com/MontiCore/monticore -->

protected <@printPortClass ast.getSymbol()/> ${ast.getName()};

<@printGetter ast.getSymbol()/>

<@printSetter ast.getSymbol()/>

<#macro printGetter port>
public <@printPortClass port/> get${port.getName()?cap_first}() {
  return this.${port.getName()};
}
</#macro>

<#macro printSetter port>
public void set${port.getName()?cap_first}(<@printPortClass port/> ${port.getName()}) {
  this.${port.getName()} = ${port.getName()};
}
</#macro>

<#macro printPortClass port>
  <#if port.getType().isPrimitive()>
    <@printPrimitivePortClass port/>
  <#else>
    <@printObjectPortClass port/>
  </#if>
</#macro>

<#macro printPrimitivePortClass port>montiarc.rte.timesync.${port.getType().print()?cap_first}<#if port.isIncoming()>In<#else>Out</#if>Port</#macro>

<#macro printObjectPortClass port>montiarc.rte.timesync.<#if port.isIncoming()>IInPort<#else>IOutPort</#if><<@printPortObjectType port/>></#macro>

<#macro printPortObjectType port>
  <#if port.getType().isTypeVariable()>
    ${port.getType().print()}
  <#else>
    ${port.getType().printFullName()}
  </#if>
</#macro>
