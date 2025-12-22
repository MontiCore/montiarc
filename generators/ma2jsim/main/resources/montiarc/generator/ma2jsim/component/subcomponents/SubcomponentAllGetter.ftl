<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getComponentHelper().getModeAutomaton(ast)>
<#assign hasOnlyOneVariant = helper.getVariantHelper().getVariants(ast)?size == 1>

@Override
public java.util.List${"<"}montiarc.rte.component.SimComponent${">"} getAllSubcomponents() {
  final java.util.ArrayList${"<"}montiarc.rte.component.SimComponent${">"} allSubcomponentList = new java.util.ArrayList<>();

  <#if hasOnlyOneVariant>
    <#list ast.getSymbol().getSubcomponents() as subcomponent>
      allSubcomponentList.add(<@subCompAccessor subcomponent/>);
    </#list>
  <#else>
    switch (this.variantID) {
      <#list helper.getVariantHelper().getVariants(ast) as variant>
        case ${helper.getVariantHelper().variantSuffix(variant)}:
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
    <#list helper.getModeHelper().getModes(modeAutomatonOpt.get()) as mode>
      case ${mode.getName()}:
        <#list helper.getModeHelper().getInstancesFromMode(mode) as modeSub>
          allSubcomponentList.add(<@modeSubCompAccessor modeSub.getSymbol() mode/>);
        </#list>
        break;
    </#list>
  }
  </#if>

  return allSubcomponentList;
}

<#macro subCompAccessor subSymbol>
  <#assign variantSuffix = helper.getVariantHelper().subcomponentVariantSuffix(ast, subSymbol)>
  this.${prefixes.subcomp()}${subSymbol.getName()}${variantSuffix}()
</#macro>

<#macro modeSubCompAccessor subSymbol mode>
  <#assign variantSuffix = helper.getVariantHelper().subcomponentVariantSuffix(ast, subSymbol)>
  this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${variantSuffix}()
</#macro>
