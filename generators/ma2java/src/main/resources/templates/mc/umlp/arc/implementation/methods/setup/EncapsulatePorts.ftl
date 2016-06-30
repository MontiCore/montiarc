${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connectorSym", "de.montiarc.generator.codegen.PortHelper portHelper", "de.montiarc.generator.codegen.GeneratorHelper generatorHelper")}

<#if portHelper.needsEncapsulation(connectorSym)>
  <#if connectorSym.getTargetPort().getName() != "">
    this.${generatorHelper.printType(connectorSym.getSourcePort.getReferenceType())?uncap_first}.add((${glex.getGlobalValue("IInSimPort")}) ${generatorHelper.printType(connectorSym.getTargetPort().getReferenceType())?uncap_first}get${connectorSym.getTargetPort.getName()?cap_first}());
  </#if>
</#if>