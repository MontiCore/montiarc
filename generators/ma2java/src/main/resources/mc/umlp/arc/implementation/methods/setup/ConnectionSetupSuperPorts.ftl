${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#list portHelper.getIncomingPortsOfSuperComponentToConnect(compSym) as port>
    
    ((${glex.getGlobalVar("IInSimPort")}) this.get${port.getName()?cap_first}()).setConnected();
</#list>    
