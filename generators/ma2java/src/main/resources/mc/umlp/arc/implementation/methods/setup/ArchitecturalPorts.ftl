${tc.params("boolean isDecomposed", "de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.PortHelper portHelper")}

<#if isDecomposed && portSym.isIncoming() && portHelper.getReceivers(portSym, compSym)?size != 1>
        this.${portSym.getName()?uncap_first} = scheduler.createForwardPort();
        this.${portSym.getName()?uncap_first}.setup(this, scheduler);
</#if>