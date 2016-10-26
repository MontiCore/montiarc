${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if compSym.isDecomposed()&&compSym.getBehaviorKind().isTimed()>
    /* (non-Javadoc)
     * @see ${glex.getGlobalVar("IComponent")}#getLocalTime()
     */
    @Override
    public int getLocalTime() {
        int _result = ${helper.getLocalTime(compSym)};
        <#--${op.includeTemplates(getLocalTimeHook, ast)}-->
        return _result;
    }
</#if>