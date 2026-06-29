<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariantHelper().getVariants(ast) as variant>
<#if !variant.isAtomic()>
protected void <@MethodNames.subCompSetup/>${helper.getVariantHelper().variantSuffix(variant)}(montiarc.rte.oracle.OracleFactory oracleFactory) {
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

    <#list variant.getSubcomponents() as subcomponent>
        <#assign subCompFieldName = prefixes.subcomp() + subcomponent.getName() + helper.getVariantHelper().subcomponentVariantSuffix(ast, subcomponent)>
        <#assign subCompType><@Util.getCompTypeString subcomponent.getType() "${suffixes.compImpl()}"/></#assign>
        <#assign subCompBuilder><@Util.getCompTypeString subcomponent.getType() "${suffixes.comp()}${suffixes.builder()}"/></#assign>

        this.${subCompFieldName} = (${subCompType}) new ${subCompBuilder}()
        .setName(this.getName() + "." + "${subcomponent.getName()}")
        .setScheduler(this.getScheduler())
        .setSuperComponent(this)
        .setOracleFactory(oracleFactory)
        <#list helper.getComponentHelper().getArgNamesMappedToExpressions(subcomponent.getAstNode()) as name, expression>
            .${prefixes.setterMethod()}${prefixes.parameter()}${name}(${javaPrinter.generateCode(expression)})
        </#list>
        <#list helper.getVariantHelper().getFeaturesMappedToBool(subcomponent) as feature, value>
            .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
        .build();
    </#list>
}
</#if>
</#list>
