<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu")}
<#include "/templates/Utils.ftl">
${p}
public interface ${ContextClass}
    extends ${fmu.getName()}Output,
    ${fmu.getName()}Parameters,
    ${fmu.getName()}Fields,
    ${fmu.getName()}Features,
    montiarc.rte.oracle.OracleOwner {
}

interface ${fmu.getName()}Input {
<#list inputVars as v>
  <#assign boxedType = "java.lang." + toBoxedJavaType(v.getType())>
  montiarc.rte.port.InPort<${boxedType}> port_${sanitizeName(v.getName())}();
</#list>
}

interface ${fmu.getName()}Output {
<#list outputVars as v>
  <#assign boxedType = "java.lang." + toBoxedJavaType(v.getType())>
  montiarc.rte.port.OutPort<${boxedType}> port_${sanitizeName(v.getName())}();
</#list>
}

interface ${fmu.getName()}Parameters {
<#list fixedParamVars as v>
  ${toJavaType(v.getType())} param_${sanitizeName(v.getName())}();
</#list>
}

interface ${fmu.getName()}Fields {}

interface ${fmu.getName()}Features {}
