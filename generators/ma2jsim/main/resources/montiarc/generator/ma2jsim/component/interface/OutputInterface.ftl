<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.output()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> {

  default java.util.List${"<"}montiarc.rte.port.TimeAwareOutPort${"<?>>"} getAllOutPorts() {
    return java.util.List.of(<#list ast.getSymbol().getAllOutgoingPorts() as portSym>${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}()<#sep>, </#sep></#list>);
  }

  <#list ast.getSymbol().getAllOutgoingPorts() as portSym>
    <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>> ${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}();
  </#list>
}