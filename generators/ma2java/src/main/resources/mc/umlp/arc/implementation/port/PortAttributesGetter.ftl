${tc.params("String returnType", "String returnValue", "de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol port", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol component")}

    public ${returnType} get${port.getName()?cap_first}() {
        ${returnType} _port = null;
    <#if port.isOutgoing() && component.isAtomic()>
        if (${returnValue} == null) {
            ${returnValue} = getScheduler().createOutPort();
        }
    </#if>
    <#-- ${op.includeTemplates(getPortHook, ast)} -->
    <#if returnValue??>
        _port = ${returnValue};    
    <#else>    
        // TODO unconnected port ${port.getName()}
    </#if>
        return _port;
    }
