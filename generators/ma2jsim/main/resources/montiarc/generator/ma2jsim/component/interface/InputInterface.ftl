<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
interface ${ast.getName()}${suffixes.input()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> {

  <#list ast.getSymbol().getAllIncomingPorts() as portSym>
    <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>> ${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}();
  </#list>
}
