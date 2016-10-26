${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

    <#if helper.noIncomingPorts(compSym)>
    <#if compSym.isAtomic()>
    private ${glex.getGlobalVar("IInSimPort")}<Object> _${glex.getGlobalVar("CODEGEN_TIME_IN_PORTNAME")};
    <#elseif compSym.isDecomposed() && !helper.getAdditionalReceiverPort(compSym)??>
    private sim.port.IForwardPort<Object> _${glex.getGlobalVar("CODEGEN_TIME_IN_PORTNAME")};
    </#if>
    
    @Override
    public ${IInPort}<Object> _get${CODEGEN_TIME_IN_PORTNAME?cap_first}() {
    <#if compSym.isAtomic() || !helper.getAdditionalReceiverPort(compSym)?? >
        return _sourceTickPort;
    <#else>
        return ${helper.getAdditionalReceiverPort(compSym)?uncap_first}._get${glex.getGlobalVar("CODEGEN_TIME_IN_PORTNAME")?cap_first}();
    </#if>
    }
    
    </#if>
