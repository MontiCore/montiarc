<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

<#if !(hasOnlyOneVariant && prettyPrinter.prettyprintCondition(helper.getVariants(ast)[0]) == "true")>
protected final int variantID;

protected int determineVariant() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list helper.getVariants(ast) as variant>
    if (${prettyPrinter.prettyprintCondition(variant)}) {
      <#if hasOnlyOneVariant>
      return 0;
      <#else>
      return ${helper.variantSuffix(variant)};
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
