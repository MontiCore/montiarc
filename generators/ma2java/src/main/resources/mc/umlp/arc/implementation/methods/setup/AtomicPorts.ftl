${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "boolean isSingleIn")}


<#if compSym.isAtomic() && !isSingleIn && portSym.isIncoming()>
        this.${portSym.getName()?uncap_first} = scheduler.createInPort();
        this.${portSym.getName()?uncap_first}.setup(this, scheduler);
</#if>