<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public interface ${ast.getName()}${suffixes.events()}<#if isTop>${suffixes.top()}</#if> {
<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  void ${prefixes.message()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}();
</#list>
}