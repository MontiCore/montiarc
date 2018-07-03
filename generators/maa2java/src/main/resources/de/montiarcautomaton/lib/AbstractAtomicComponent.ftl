${tc.signature("helper", "packageName", "compName", "compInputName", "compResultName", 
"configParams", "imports")}

package ${packageName};

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
<#list imports as import>
import ${import.getStatement()}<#if import.isStar()>.*</#if>;
</#list>

class ${compName} <#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> implements IComputable<${compInputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>, ${compResultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if>> {

  public ${compName}(<#list configParams as param>${helper.getParamTypeName(param)} ${param.getName()}<#sep>, </#list>) {
        throw new Error("Invoking constructor on abstract implementation ${packageName}.${compName}");
    }

    public ${compResultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> getInitialValues() {
        throw new Error("Invoking getInitialValues() on abstract implementation ${packageName}.${compName}");
    }

    public ${compResultName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> compute(${compInputName}<#if helper.isGeneric()><<#list helper.getGenericParameters() as param>${param}<#sep>,</#list>></#if> input) {
        throw new Error("Invoking compute() on abstract implementation ${packageName}.${compName}");
    }

}