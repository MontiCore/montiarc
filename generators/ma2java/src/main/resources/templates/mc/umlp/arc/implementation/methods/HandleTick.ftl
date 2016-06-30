${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if compSym.isAtomic()>
    /* (non-Javadoc)
     * @see ${glex.getGlobalValue("AComponent")}#handleTick()
     */
    @Override
    public void handleTick() {
        <#-- ${op.includeTemplates(handleTickStartHook, ast)}-->
    <#if glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous()>
        triggerTimeSync();
    </#if>    
    <#list compSym.getOutgoingPorts() as port>
        ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.HandleTickPorts", [helper.printType(port.getTypeReference()), port.getName()])}
    </#list>
    <#if compSym.getSuperComponent().isPresent()>
        super.handleTick();
    <#elseif glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimed()>
        incLocalTime();
        timeStep();
    </#if>
        <#-- ${op.includeTemplates(handleTickEndHook, ast)} -->
    }
    
</#if>