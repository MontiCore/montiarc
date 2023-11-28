<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

interface ${ast.getName()}${suffixes.input()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> {

  default java.util.List${"<"}montiarc.rte.port.ITimeAwareInPort${"<?>>"} getAllInPorts() {
    return java.util.List.of(<#list ast.getSymbol().getAllIncomingPorts() as portSym>${prefixes.port()}${portSym.getName()}()<#sep>, </#sep></#list>);
  }

  <#list ast.getSymbol().getAllIncomingPorts() as portSym>
    <@Util.getStaticPortClass portSym ast.getSymbol().isAtomic()/><<@Util.getPortTypeString portSym.getType()/>> ${prefixes.port()}${portSym.getName()}();
  </#list>
}