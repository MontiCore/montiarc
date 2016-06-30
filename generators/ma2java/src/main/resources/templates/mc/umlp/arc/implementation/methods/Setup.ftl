${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper generatorHelper", "de.montiarc.generator.codegen.PortHelper portHelper")}

    /* (non-Javadoc)
     * @see ${glex.getGlobalValue("IComponent")}#setup(${glex.getGlobalValue("IScheduler")}, ${glex.getGlobalValue("ISimulationErrorHandler")})
     */
    @Override
<#if compSym.isDecomposed()>    
    @SuppressWarnings("rawtypes")
</#if>    
    public void setup(${glex.getGlobalValue("IScheduler")} scheduler, ${glex.getGlobalValue("ISimulationErrorHandler")} errorHandler) {
<#if (compSym.isAtomic() && portHelper.isSingleIn(compSym)) || compSym.getSuperComponent().isPresent()>        
        super.setup(scheduler, errorHandler);
</#if>	
        <#-- ${op.includeTemplates(setupStartHook, ast)}-->
        // set scheduler
        setScheduler(scheduler);
        // set the errorHandler
        setErrorHandler(errorHandler);
        setComponentName("${compSym.getFullName()}");
        <#list compSym.getPorts() as port>
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.ArchitecturalPorts", [compSym.isDecomposed(), port, compSym, portHelper])}
	    </#list>
	    
	    <#list compSym.getSubComponents() as subcomponent>
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.ArchitecturalSubcomponents", [compSym, subcomponent, generatorHelper.getSubComponentFactoryName(subcomponent), generatorHelper])}
	    </#list>
	    
	    <#list compSym.getPorts() as port>
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.AtomicPorts", [port, compSym, portHelper.isSingleIn(compSym)])}
	    </#list>
	    
	    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.TimeInPort", [compSym, generatorHelper])}

        <#list compSym.getConnectors() as connector>
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.ConnectionSetup", [connector, portHelper])}
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.Connections", [connector, portHelper])}
	    </#list>
	    
	    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.ConnectionSetupSuperPorts", [compSym, portHelper])}
	    <#list compSym.getConnectors() as connector>
	      ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.setup.EncapsulatePorts", [connector, portHelper, generatorHelper])}
	    </#list>
	    
<#--    	    
	    
    ${op.includeTemplates(setupEndHook, ast)} --> 
    }    