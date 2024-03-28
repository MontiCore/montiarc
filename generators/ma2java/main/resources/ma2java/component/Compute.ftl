<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}

@Override
public void compute() {

  // log input values
  <#list comp.getIncomingPorts() as port>
    montiarc.rte.log.Log.trace(
      "Value of input port ${port.getName()} = "
      + this.get${port.getName()?cap_first}().getValue()
    );
  </#list>

  // inputs
  <@printLocalInputVariables comp/>

  // outputs
  <@printLocalOutputVariables comp/>

  // compute
  ${compHelper.printStatement(ast.getMCBlockStatement())}

  // result
  <@printSetOutput comp/>

  <@printSynchronize comp/>

   // log output values
  <#list comp.getOutgoingPorts() as port>
    montiarc.rte.log.Log.trace(
      "Value of output port ${port.getName()} = "
      + this.get${port.getName()?cap_first}().getValue()
    );
  </#list>
}

<#macro printLocalInputVariables comp>
  <#list comp.getAllIncomingPorts() as port>
    final <@printType port.getType()/> ${port.getName()} = this.get${port.getName()?cap_first}().getValue();
  </#list>
</#macro>

<#macro printLocalOutputVariables comp>
  <#list comp.getAllOutgoingPorts() as port>
    <@printType port.getType()/> ${port.getName()} = <@printDefaultValue port.getType()/>;
  </#list>
</#macro>

<#macro printSetOutput comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().setValue(${port.getName()});
  </#list>
</#macro>

<#macro printType type>
  <#if type.isPrimitive() || type.isTypeVariable()>${type.print()}<#else>${type.printFullName()}</#if>
</#macro>

<#macro printSynchronize comp>
  <#list comp.getAllOutgoingPorts() as port>
    this.get${port.getName()?cap_first}().sync();
  </#list>
</#macro>

<#macro printDefaultValue portType><#if !portType.isPrimitive()>null<#elseif portType.print()?matches("boolean")>false<#else>0</#if></#macro>
