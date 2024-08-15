<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)>
<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

public java.util.List${"<"}montiarc.rte.component.Component${">"} getAllSubcomponents() {
  final java.util.ArrayList${"<"}montiarc.rte.component.Component${">"} allSubcomponentList = new java.util.ArrayList<>();

  <#if hasOnlyOneVariant>
    <#list ast.getSymbol().getSubcomponents() as subcomponent>
      allSubcomponentList.add(<@subCompAccessor subcomponent/>);
    </#list>
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
        <#list variant.getSubcomponents() as subcomponent>
          allSubcomponentList.add(<@subCompAccessor subcomponent/>);
        </#list>
          break;
      </#list>
      default: assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    }
  </#if>

  <#if modeAutomatonOpt.isPresent()>
  switch (this.modeAutomaton.currentMode) {
    <#list helper.getModes(modeAutomatonOpt.get()) as mode>
      case ${mode.getName()}:
        <#list helper.getInstancesFromMode(mode) as modeSub>
          allSubcomponentList.add(<@modeSubCompAccessor modeSub.getSymbol() mode/>);
        </#list>
        break;
    </#list>
  }
  </#if>

  return allSubcomponentList;
}

<#macro subCompAccessor subSymbol>
  <#assign variantSuffix = helper.subcomponentVariantSuffix(ast, subSymbol)>
  this.${prefixes.subcomp()}${subSymbol.getName()}${variantSuffix}()
</#macro>

<#macro modeSubCompAccessor subSymbol mode>
  <#assign variantSuffix = helper.subcomponentVariantSuffix(ast, subSymbol)>
  this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${variantSuffix}()
</#macro>
