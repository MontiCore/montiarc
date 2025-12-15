<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
interface ${ast.getName()}${suffixes.parameters()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>{

<#list ast.getHead().getArcParameterList() as param>
    <@Util.getTypeString param.getSymbol().getType()/> ${prefixes.parameter()}${param.getName()}();
</#list>
}
