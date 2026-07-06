<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu")}
<#include "/templates/Utils.ftl">
${p}
public interface ${eventClass} extends montiarc.rte.behavior.Behavior<${syncMsgClass}>{
<#list inputVars as v>
  void msg_${sanitizeName(v.getName())}(${toJavaType(v.getType())} msg);
</#list>
}
