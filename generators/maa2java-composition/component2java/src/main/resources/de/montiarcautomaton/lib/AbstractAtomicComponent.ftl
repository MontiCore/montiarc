${tc.params("de.montiarcautomaton.generator.helper.ComponentHelper helper", "String packageName", 
"String compName", "String compInputName", "String compResultName", "java.util.Collection<de.monticore.symboltable.types.JFieldSymbol> configParams")}

package ${packageName};

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

class ${compName} implements IComputable<${compInputName}, ${compResultName}> {

  public ${compName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
        throw new Error("Invoking constructor on abstract implementation ${packageName}.${compName}");
    }

    public ${compResultName} getInitialValues() {
        throw new Error("Invoking getInitialValues() on abstract implementation ${packageName}.${compName}");
    }

    public ${compResultName} compute(${compInputName} input) {
        throw new Error("Invoking compute() on abstract implementation ${packageName}.${compName}");
    }

}