<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

<@printInit/>

@Override
public void compute() {
  // inputs
  <@printLocalInputVariables comp/>

  // outputs
  <@printLocalOutputVariables comp/>

  // compute
  ${compHelper.printStatement(ast.getMCBlockStatement())}

  // result
  <@printSetOutput comp/>
}

<#macro printLocalInputVariables comp>
  <#list comp.getAllIncomingPorts() as port>
    final ${compHelper.getRealPortTypeString(port)} ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    ${compHelper.getRealPortTypeString(port)} ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>

<#macro printInit>
  @Override
  public void init() { }
</#macro>
