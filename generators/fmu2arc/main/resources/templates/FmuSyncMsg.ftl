<#-- (c) https://github.com/MontiCore/monticore -->
${signature("fmu")}
<#include "/templates/Utils.ftl">
${p}
public class ${syncMsgClass} {
<#list inputVars as v>
  public final ${toJavaType(v.getType())} ${sanitizeName(v.getName())};
</#list>

  public ${syncMsgClass}(
<#list inputVars as v>
    ${toJavaType(v.getType())} ${sanitizeName(v.getName())}<#if v_has_next>,</#if>
</#list>
  ) {
<#list inputVars as v>
    this.${sanitizeName(v.getName())} = ${sanitizeName(v.getName())};
</#list>
  }
}
