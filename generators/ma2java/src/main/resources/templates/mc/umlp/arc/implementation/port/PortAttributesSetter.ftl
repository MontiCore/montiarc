${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.PortHelper portHelper", "de.montiarc.generator.codegen.GeneratorHelper generatorHelper")}
<#if portSym.isOutgoing()>
    public void set${portSym.getName()?cap_first}(${glex.getGlobalValue("IPort")}<${generatorHelper.printType(portSym.getTypeReference())}> port) {
    <#if compSym.isAtomic()>
    <#assign portName = "${portSym.getName()?uncap_first}">        
        if (this.${portName} == null) {
            this.${portName} = port;            
        }
        else {
            ((${glex.getGlobalValue("IOutSimPort")}<${generatorHelper.printType(portSym.getTypeReference())}>) this.${portName}).addReceiver(port);
        }
    <#elseif portHelper.getSetSubCompPort(portSym, compSym)??>
        this.${portHelper.getSetSubCompPort(portSym, compSym)}(port);
    <#else>
        // TODO unconnected port ${portSym.getName()}
    </#if>
       <#-- ${op.includeTemplates(setPortHook, ast)} -->
    }
</#if>
