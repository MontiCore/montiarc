${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if portSym.isOutgoing() && compSym.isAtomic()>
    /**
     * Is used to send messages through the outgoing port 
     * ${portSym.getName()?uncap_first}.
     */
    protected void send${portSym.getName()?cap_first}(final ${helper.printType(portSym.getTypeReference())} message) {
    <#if glex.getGlobalVar("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous()>
        if (${portSym.getName()}Buffer != null) {
            getErrorHandler().addReport(new sim.error.ArcSimProblemReport(
                            sim.error.ArcSimProblemReport.Type.WARNING,
                            "Time-synchronous component tries to send multiple messages withing a single time frame on port ${portSym.getName()}.",
                            getLocalTime(),
                            getComponentName()));
        }
        else {
            ${portSym.getName()}Buffer = message;
            this.get${portSym.getName()?cap_first}().send(sim.generic.Message.of(message));
        }
    <#else>
        this.get${portSym.getName()?cap_first}().send(sim.generic.Message.of(message));
    </#if>
        <#-- ${op.includeTemplates(sendOutPortHook, ast)} --> 
    }
</#if>