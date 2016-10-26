${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol connector", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#if connector.getTargetPort().get().isIncoming()>
  ((${glex.getGlobalVar("IInSimPort")}) ${portHelper.printTargetName(connector)?uncap_first}.get${connector.getTargetPort().get().getName()?cap_first}()).setConnected();
</#if>