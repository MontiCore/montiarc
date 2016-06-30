${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "String _package", "String comments", "String modifier", "String prefix", "String superComponent", "String fqCompInterfaceWithTypeParameters", "de.montiarc.generator.codegen.GeneratorHelper helper", "de.montiarc.generator.codegen.PortHelper portHelper", "de.monticore.lang.montiarc.helper.Timing
 timing", "String typeParameters")}

package ${_package};

/**
 * ${comments}
 * <br><br>
 * Java representation of component ${compSym.getName()}.<br>
 * <br>
 * Generated with MontiArc.<br>
 <#--* @date ${TIME_NOW}<br>-->
 *
 */
public ${modifier} class ${prefix}${compSym.getName()}${typeParameters} <#t>
    extends ${superComponent} <#t>
    implements ${_package}.interfaces.I${compSym.getName()}${helper.printFormalTypeParameters(compSym.getFormalTypeParameters())}, ${glex.getGlobalValue("ISimComponent")} <#t>
{
    <#-- all templates that are embedded into generated components -->
    <#-- ${op.includeTemplates(subcomponentAttributes, ast.getSubComponentInstances())} -->
    
    <#list compSym.getSubComponents() as subcomponent>
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.SubcomponentAttributes", [helper.getSubcomponentInterface(subcomponent), subcomponent.getName()])}
    </#list>
    
    <#list compSym.getPorts() as portSym>
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortAttributes", [portHelper.getPortType(glex, portSym, compSym), portHelper.getPortName(compSym, portSym)])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortAttributesGetter", [portHelper.getPortReturnType(glex, portSym), portHelper.getPortReturnValue(compSym, portSym), portSym, compSym])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortAttributesSetter", [portSym, compSym, portHelper, helper])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortInDelegate", [compSym, portSym, helper])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortOutDelegate", [compSym, portSym, helper])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.TimeSyncMessageBuffer", [portSym, helper])}
       ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.SimplePortInDelegate", [portSym, portHelper.isSingleIn(compSym), helper])}
    </#list>
    
    <#list compSym.getConfigParameters() as configParam>
      ${tc.includeArgs("templates.mc.umlp.arc.implementation.ConfigurationParameters", [helper.printType(configParam.getType()), configParam.getName()])}
    </#list>
    
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.port.PortTimeIn", [compSym, helper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.Constructor", [compSym, helper, prefix])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.GetLocalTime", [compSym, helper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.HandleMessage", [compSym, helper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.HandleTimeSyncMessage", [compSym, helper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.HandleTick", [compSym, helper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.Setup", [compSym, helper, portHelper])}
    ${tc.includeArgs("templates.mc.umlp.arc.implementation.methods.TimeSyncTimeIncreased", [compSym, helper])}
    

    <#if compSym.isDecomposed()>
    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalValue("IComponent")}#handleMessage(${glex.getGlobalValue("IInPort")}, sim.generic.Message<?>)
     */
    @Override
    public void handleMessage(${glex.getGlobalValue("IInPort")}<?> port, sim.generic.Message<?> message) {
        <#if compSym.getSuperComponent().isPresent()>
        super.handleMessage(port, message);
        <#else>
        // this method is not used in architectural components
        </#if>
    }

    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalValue("AComponent")}#handleTick()
     */
    @Override
    public void handleTick() {
        <#if compSym.getSuperComponent().isPresent()>
        super.handleTick();
        <#else>
        // this method is not used in architectural components
        </#if>
    }
    
    <#if glex.getGlobalValue("TIME_PARADIGM_STORAGE_KEY").isTimed()>
    /*
     * (non-Javadoc)
     * @see ${glex.getGlobalValue("ISimComponent")}#timeStep()
     */    
    @Override
    protected void timeStep() {
        <#if compSym.getSuperComponent().isPresent()>
        super.timeStep();
        <#else>
        // this method is not used in architectural components
        </#if>
    }
        </#if>
    </#if>
    
     /*
     * Has no function as it is not part of the MontiArc4 language.  
     * @see ${glex.getGlobalValue("IComponent")}#checkConstraints()
     */
    @Override
    public void checkConstraints() {
    }
    
    <#-- If invariants are available in MontiArc4 remove the comment and add arguments to this statement: 
    ${tc.writeArgs("mc.umlp.arc.implementation.methods.CheckConstraints"), [...]]} 
    -->
    
    
}
