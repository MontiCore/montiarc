<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isTop")}

<@printClass ast.getName() isTop/>
<@printTypeParameters comp = ast.getSymbol()/>
<@printExtensions comp = ast.getSymbol()/>
 implements montiarc.rte.timesync.IComponent

<#macro printClass name isTop>
  <#if isTop>
    public abstract class ${name}TOP<#t>
  <#else>
    public class ${name}<#t>
  </#if>
</#macro>

<#macro printExtensions comp>
  <#if !comp.isEmptySuperComponents()>
    extends ${comp.getSuperComponents(0).getTypeInfo().getFullName()}
  </#if>
</#macro>

<#macro printTypeParameters comp>
  <#if comp.hasTypeParameter()>
    <<#list comp.getTypeParameters() as typeParameter>
      ${typeParameter.getName()}<#sep>, </#sep>
    </#list>>
  </#if>
</#macro>
