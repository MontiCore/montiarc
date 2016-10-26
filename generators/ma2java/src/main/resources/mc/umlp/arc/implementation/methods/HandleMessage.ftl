${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if !glex.getGlobalVar("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous() && compSym.isAtomic()>
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
            treat${port.getName()?cap_first}((${helper.printType(port.getTypeReference())}) message.getData());
        }
    <#assign ifType = "else if">
</#list>
        <#-- ${op.includeTemplates(handleMessageHook, ast)}-->
    }

</#if>