${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if glex.getGlobalVar("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous() && compSym.isAtomic()>
    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalVar("IComponent")}#handleMessage(${glex.getGlobalVar("IInPort")}, sim.generic.Message<?>)
     */
    @Override
    public void handleMessage(${glex.getGlobalVar("IInPort")}<?> port, sim.generic.Message<?> message) {
<#if compSym.getSuperComponent().isPresent()>
        super.handleMessage(port, message);
</#if>        
<#assign ifType = "if">
<#list compSym.getIncomingPorts() as port>   
        ${ifType} (port == get${port.getName()?cap_first}()) {
            if (${port.getName()}Buffer != null) {
                getErrorHandler().addReport(new sim.error.ArcSimProblemReport(
                                sim.error.ArcSimProblemReport.Type.WARNING,
                                "Time-synchronous component received multiple messages withing a single time frame on port ${port.getName()}.",
                                getLocalTime(),
                                getComponentName()));
            }
            ${port.getName()}Buffer = (${helper.printType(port.getTypeReference())}) message.getData();
        }
    <#assign ifType = "else if">
</#list>
        <#-- ${op.includeTemplates(handleMessageTimeSyncHook, ast)}-->
    }

</#if>