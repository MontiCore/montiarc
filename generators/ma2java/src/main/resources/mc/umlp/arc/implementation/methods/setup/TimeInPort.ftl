${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if helper.requiresPortTimeIn(compSym) && !helper.getAdditionalReceiverPort(compSym)??>
    <#if compSym.isAtomic()>
        this._${glex.getGlobalValue("CODEGEN_TIME_IN_PORTNAME")} = scheduler.createInPort();
    <#else>
        this._${glex.getGlobalValue("CODEGEN_TIME_IN_PORTNAME")} = scheduler.createForwardPort();
    </#if>
        this._${glex.getGlobalValue("CODEGEN_TIME_IN_PORTNAME")}.setup(this, scheduler);
</#if>
<#if !helper.getAdditionalReceiverPort(compSym)??>
    <#list helper.getSubWithoutPorts(compSym) as r>   
        this.${helper.getSender(compSym)}.add((${glex.getGlobalValue("IInSimPort")}<Object>) ${r}._get${glex.getGlobalValue("CODEGEN_TIME_IN_PORTNAME")?cap_first}());
    </#list>
</#if>