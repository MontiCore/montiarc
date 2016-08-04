${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.montiarc.generator.codegen.GeneratorHelper helper", "String prefix")}

    public ${prefix}${compSym.getName()}(${helper.printConfigParameters(compSym.getConfigParameters())}) {
        super(${helper.printSuperComponentConfigParametersNamesForSuperCall(compSym)});
        <#list compSym.getConfigParameters() as configParam>
          ${_templates.mc.umlp.arc.implementation.methods.ConstructorParameters.generate(configParam.getName())}
        </#list>
        <#-- ${op.includeTemplates(constructorHook, ast)} -->
    }
