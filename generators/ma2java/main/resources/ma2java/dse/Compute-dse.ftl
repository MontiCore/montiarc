<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

@Override
public void compute() {
  // inputs
  <@printLocalInputVariables comp/>

  // outputs
  <@printLocalOutputVariables comp/>

  // compute
  ${compHelperDse.printStatement(ast.getMCBlockStatement())}

  // result
  <@printSetOutput comp/>

  <@printSynchronize comp/>
}

<#macro printLocalInputVariables comp>
  <#list comp.getAllIncomingPorts() as port>
    final montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}> ${port.getName()} = this.get${port.getName()?cap_first}();
  </#list>
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    montiarc.rte.dse.AnnotatedValue<Expr<${compHelperDse.getPortTypeSort(port)}>, ${compHelper.getRealPortTypeString(port)}> ${port.getName()} = montiarc.rte.dse.AnnotatedValue.newAnnoValue(null, null);
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>

<#macro printSynchronize comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().sync();
  </#list>
</#macro>
