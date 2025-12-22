<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
interface ${ast.getName()}${suffixes.features()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

<#list helper.getComponentHelper().getFeatures(ast) as feature>
    boolean ${prefixes.feature()}${feature.getName()}();
</#list>
}
