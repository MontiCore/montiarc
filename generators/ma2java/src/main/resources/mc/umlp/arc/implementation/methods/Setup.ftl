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
	      ${_templates.mc.umlp.arc.implementation.methods.setup.ArchitecturalPorts.generate(compSym.isDecomposed(), port, compSym, portHelper)}
	    </#list>
	    
	    <#list compSym.getSubComponents() as subcomponent>
	      ${_templates.mc.umlp.arc.implementation.methods.setup.ArchitecturalSubcomponents.generate(compSym, subcomponent, generatorHelper.getSubComponentFactoryName(subcomponent), generatorHelper)}
	    </#list>
	    
	    <#list compSym.getPorts() as port>
	      ${_templates.mc.umlp.arc.implementation.methods.setup.AtomicPorts.generate(port, compSym, portHelper.isSingleIn(compSym))}
	    </#list>
	    
	    ${_templates.mc.umlp.arc.implementation.methods.setup.TimeInPort.generate(compSym, generatorHelper)}

        <#list compSym.getConnectors() as connector>
	      ${_templates.mc.umlp.arc.implementation.methods.setup.ConnectionSetup.generate(connector, portHelper)}
	      ${_templates.mc.umlp.arc.implementation.methods.setup.Connections.generate(connector, portHelper)}
	    </#list>
	    
	    ${_templates.mc.umlp.arc.implementation.methods.setup.ConnectionSetupSuperPorts.generate(compSym, portHelper)}
	    <#list compSym.getConnectors() as connector>
	      ${_templates.mc.umlp.arc.implementation.methods.setup.EncapsulatePorts.generate(connector, compSym, portHelper, generatorHelper)}
	    </#list>
	    
<#--    	    
	    
    ${op.includeTemplates(setupEndHook, ast)} --> 
    }    