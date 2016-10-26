${tc.params("de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol compSym", "de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol subComponent", "String factoryName", "de.montiarc.generator.codegen.GeneratorHelper helper")}

        this.${subComponent.getName()?uncap_first} = ${factoryName}.create(${helper.printConfigParametersNames(subComponent.getComponentType().getConfigParameters())});
        this.${subComponent.getName()?uncap_first}.setup(scheduler, errorHandler);
        ((${glex.getGlobalVar("ISimComponent")}) this.${subComponent.getName()?uncap_first}).setComponentName(
            "${subComponent.getComponentType().getFullName()} ${subComponent.getName()?uncap_first} in ${compSym.getName()}");
