${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous()>
    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalValue("ATimedComponent")}#timeStep()
     */
    @Override
    protected void timeStep() {
<#if compSym.getSuperComponent().isPresent()>
        super.timeStep();
</#if>        
<#foreach port in compSym.getIncomingPorts()>   
        // erase in port buffer
        ${port.getName()}Buffer = null;
</#foreach>
<#foreach port in compSym.getOutgoingPorts()>   
        // erase out port buffer
        ${port.getName()}Buffer = null;
</#foreach>              
    }
    
    protected void triggerTimeSync() {      
<#assign comma = "">
        timeStep(
<#foreach port in compSym.getIncomingPorts()>   
            ${comma}${port.getName()}Buffer
    <#assign comma = ", ">
</#foreach>
        );
    }

    protected abstract void timeStep(
 <#assign comma = "">
<#foreach port in compSym.getIncomingPorts()>   
        ${comma}${helper.printType(port.getTypeReference())} ${port.getName()}Buffer
    <#assign comma = ", ">
</#foreach>
    );
</#if>