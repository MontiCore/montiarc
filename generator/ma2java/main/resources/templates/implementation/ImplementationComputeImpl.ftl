<#-- (c) https://github.com/MontiCore/monticore -->
<#-- This template creates compute block implementations for components that have compute blocks. -->
<#import "/templates/util/Utils.ftl" as Utils>

<#macro printImplementationClassMethods comp compHelper identifier isTOPClass=false>
  <#assign compTypeParams><@Utils.printFormalTypeParameters comp=comp/></#assign>
  <#assign resultClass>${comp.getName()}Result${compTypeParams}</#assign>
  <#assign inputClass>${comp.getName()}Input${compTypeParams}</#assign>
  <#assign inputParam = identifier.getInputName()>

  @Override
  public ${resultClass} getInitialValues() {
    return new ${resultClass}();
  }

  @Override
  public ${resultClass} compute(${inputClass} ${inputParam}) {
    ${resultClass} ${identifier.getResultName()} = new ${resultClass}();

    // working copies of inputs
    <@printLocalVariablesFromInput comp=comp compHelper=compHelper identifier=identifier/>

    // working copies of current outputs
    <@printLocalVariablesForNewResult comp=comp compHelper=compHelper identifier=identifier/>

    // compute block
    ${compHelper.printStatement(compHelper.getComputeBehavior().get().getMCBlockStatement())}

    // set result values
    <#list comp.getAllOutgoingPorts() as port>
        ${identifier.getResultName()}.set${port.getName()?cap_first}(${port.getName()});
    </#list>

    return ${identifier.getResultName()};
  }
</#macro>

<#macro printLocalVariablesFromInput comp compHelper identifier>
    <#assign inputParam = identifier.getInputName()>
    <#list comp.getAllIncomingPorts() as port>
      final ${compHelper.getRealPortTypeString(port)} ${port.getName()} = ${inputParam}.get${port.getName()?cap_first}();
    </#list>
</#macro>

<#macro printLocalVariablesForNewResult comp compHelper identifier>
    <#assign resultParam = identifier.getResultName()>
    <#list comp.getAllOutgoingPorts() as port>
        ${compHelper.getRealPortTypeString(port)} ${port.getName()} = null;
    </#list>
</#macro>