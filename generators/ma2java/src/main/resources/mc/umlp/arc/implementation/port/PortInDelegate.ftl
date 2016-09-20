${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.montiarc.generator.codegen.GeneratorHelper generatorHelper")}

<#if !glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous() && portSym.isIncoming() && compSym.isAtomic()>
    /**
     * Is called from the simulation framework, if a message is received on 
     * port ${portSym.getName()?uncap_first}.
     */
    protected abstract void treat${portSym.getName()?cap_first}(final ${generatorHelper.printType(portSym.getTypeReference())} message);
</#if>