${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connector", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#if connector.getSourcePort().isOutgoing() && connector.getTargetPort().isIncoming()>
  ${portHelper.printSourceName(connector)?uncap_first}.set${connector.getSourcePort().getName()?cap_first}((${glex.getGlobalValue("IPort")}) ${portHelper.printTargetName(connector)?uncap_first}.get${connector.getTargetPort().getName()?cap_first}());
</#if>