<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

<#if !(hasOnlyOneVariant && prettyPrinter.prettyprintCondition(helper.getVariants(ast)[0]) == "true")>
protected final int variantID;

protected int determineVariant() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list helper.getVariants(ast) as variant>
    if (${prettyPrinter.prettyprintCondition(variant)}) {
      return ${helper.variantSuffix(variant)};
    }
  <#sep> else </#sep>
  </#list>
  else {
    assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    return -1;
  }
}
</#if>
