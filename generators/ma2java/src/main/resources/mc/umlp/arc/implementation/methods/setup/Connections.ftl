${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connector", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#if connector.getSourcePort().get().isOutgoing() && connector.getTargetPort().get().isIncoming()>
  ${portHelper.printSourceName(connector)?uncap_first}.set${connector.getSourcePort().get().getName()?cap_first}((${glex.getGlobalVar("IPort")}) ${portHelper.printTargetName(connector)?uncap_first}.get${connector.getTargetPort().get().getName()?cap_first}());
</#if>