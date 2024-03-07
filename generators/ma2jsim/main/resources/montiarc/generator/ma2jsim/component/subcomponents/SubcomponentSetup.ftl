<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list helper.getVariants(ast) as variant>
<#if !variant.isAtomic()>
protected void <@MethodNames.subCompSetup/>${helper.variantSuffix(variant)}() {
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

    <#list variant.getSubcomponents() as subcomponent>
        this.${prefixes.subcomp()}${subcomponent.getName()}${helper.subcomponentVariantSuffix(ast, subcomponent)} = new <@Util.getCompTypeString subcomponent.getType() "${suffixes.component()}${suffixes.builder()}"/>()
        .setName("${subcomponent.getName()}")
        .setScheduler(this.getScheduler())
        <#list helper.getArgNamesMappedToExpressions(subcomponent.getAstNode()) as name, expression>
            .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${prettyPrinter.prettyprint(expression)})
        </#list>
        <#list helper.getFeaturesMappedToBool(subcomponent) as feature, value>
            .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
        .build();
    </#list>
}
</#if>
</#list>