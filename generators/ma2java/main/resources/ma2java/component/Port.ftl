<#-- (c) https://github.com/MontiCore/monticore -->

protected <@printClass ast.getSymbol()/><<@printType ast.getSymbol()/>> ${ast.getName()};

<@printGetter ast.getSymbol()/>

<@printSetter ast.getSymbol()/>

<#macro printType port>
  <#if port.getType().isPrimitive()>
    ${compHelper.boxPrimitive(port.getType())}
  <#elseif port.getType().isTypeVariable()>
    ${port.getType().print()}
  <#else>
    ${port.getType().printFullName()}
  </#if>
</#macro>

<#macro printGetter port>
public <@printClass port/><<@printType port/>> get${port.getName()?cap_first}() {
  return this.${port.getName()};
}
</#macro>

<#macro printSetter port>
public void set${port.getName()?cap_first}(<@printClass port/><<@printType port/>> ${port.getName()}) {
  this.${port.getName()} = ${port.getName()};
}
</#macro>

<#macro printClass port> montiarc.rte.timesync.<#if port.isIncoming()>IPortIn<#else>IPortOut</#if></#macro>
