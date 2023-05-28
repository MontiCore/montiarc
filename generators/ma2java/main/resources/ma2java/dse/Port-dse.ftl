<#-- (c) https://github.com/MontiCore/monticore -->

protected <@printClass ast.getSymbol()/><montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort( ast.getSymbol())}>, <@printType ast.getSymbol()/>>> ${ast.getName()};

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
public <@printClass port/><montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort( ast.getSymbol())}>, <@printType ast.getSymbol()/>>> get${port.getName()?cap_first}() {
  return this.${port.getName()};
}
</#macro>

<#macro printSetter port>
public void set${port.getName()?cap_first}(<@printClass port/><montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort( ast.getSymbol())}>, <@printType ast.getSymbol()/>>> ${port.getName()}) {
  this.${port.getName()} = ${port.getName()};
}
</#macro>

<#macro printClass port> montiarc.rte.timesync.<#if port.isIncoming()>IInPort<#else>IOutPort</#if></#macro>
