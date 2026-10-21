<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#if !ast.getSymbol().isAtomic()>
protected void <@MethodNames.subCompSetup/>(montiarc.rte.oracle.OracleFactory oracleFactory) {
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

    <#list ast.getSymbol().getSubcomponents() as subcomponent>
        <#assign subCompFieldName = prefixes.subcomp() + subcomponent.getName()>
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
        .build();
    </#list>
}
</#if>

