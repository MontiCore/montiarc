${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connectorSym", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.PortHelper portHelper", "de.montiarc.generator.codegen.GeneratorHelper generatorHelper")}

<#if portHelper.needsEncapsulation(connectorSym, compSym)>
  <#if connectorSym.getTargetPort().get().getName() != "">
    this.${generatorHelper.printType(connectorSym.getSourcePort().get().getReferenceType())?uncap_first}.add((${glex.getGlobalVar("IInSimPort")}) ${generatorHelper.printType(connectorSym.getTargetPort().get().getReferenceType())?uncap_first}get${connectorSym.getTargetPort().get().getName()?cap_first}());
  </#if>
</#if>