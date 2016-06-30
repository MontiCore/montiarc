${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connector", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#if connector.getTargetPort().isIncoming()>
  ((${glex.getGlobalValue("IInSimPort")}) ${portHelper.printTargetName(connector)?uncap_first}.get${connector.getTargetPort().getName()?cap_first}()).setConnected();
</#if>