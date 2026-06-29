<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign hasOnlyOneVariant = helper.getVariantHelper().getVariants(ast)?size == 1>

<#if !(hasOnlyOneVariant && javaPrinter.generateCodeCondition(helper.getVariantHelper().getVariants(ast)[0]) == "true")>
protected final int variantID;

protected int determineVariant() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list helper.getVariantHelper().getVariants(ast) as variant>
    if (${javaPrinter.generateCodeCondition(variant)}) {
      <#if hasOnlyOneVariant>
      return 0;
      <#else>
      return ${helper.getVariantHelper().variantSuffix(variant)};
      </#if>
    }
  <#sep> else </#sep>
  </#list>
  else {
    assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    return -1;
  }
}
</#if>
