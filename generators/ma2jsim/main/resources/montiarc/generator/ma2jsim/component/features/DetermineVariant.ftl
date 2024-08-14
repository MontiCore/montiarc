<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

protected final String variantID;

protected String determineVariant() {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list helper.getVariants(ast) as variant>
    if (${prettyPrinter.prettyprintCondition(variant)}) {
      return "${helper.variantSuffix(variant)}";
    }
  <#sep> else </#sep>
  </#list>
  else throw new IllegalStateException();
}