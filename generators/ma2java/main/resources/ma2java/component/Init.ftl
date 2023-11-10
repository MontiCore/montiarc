<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

<@printInit/>

<#macro printInit>
  @Override
  public void init() {
    // outputs
    <@printLocalOutputVariables comp/>
    // execute the initial action
    ${compHelper.printStatement(ast.getMCBlockStatement())}
    // set result
    <@printSetOutput comp/>
    // provide initial value for delay ports
    <#list comp.getPortsList() as port>
      <#if port.isDelayed()>this.${port.getName()}.tick();</#if>
    </#list>
  }
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    ${compHelper.getRealPortTypeString(port)} ${port.getName()} = null;
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    if (${port.getName()} != null) this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>