${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol portSym", "de.montiarc.generator.codegen.GeneratorHelper helper")}

<#if glex.getGlobalVar("TIME_PARADIGM_STORAGE_KEY").isTimeSynchronous()>
    private ${helper.printType(portSym.getTypeReference())} ${portSym.getName()}Buffer;
</#if>