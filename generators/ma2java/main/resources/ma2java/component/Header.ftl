<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isTop")}

<@printClass ast.getName() isTop/>
<@printTypeParameters ast = ast/>
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

<#macro printTypeParameters ast>
  <#if ast.getSymbol().hasTypeParameter()>
    <<#list ast.getHead().getArcTypeParameterList() as typeParameter>${typeParameter.getName()}
      <#if !typeParameter.isEmptyUpperBound()> extends
        <#list typeParameter.getUpperBoundList() as boundEntry> ${boundEntry.printType()}</#list>
      </#if>
      <#sep>, </#sep>
    </#list>>
  </#if>
</#macro>
