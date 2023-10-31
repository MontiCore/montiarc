<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast-->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#-- TODO: does not handle features -->
protected ${ast.getName()}${suffixes.component()}<#if isTop>${suffixes.top()}</#if>(
  String name<#list ast.getHead().getArcParameterList()>,
    <#items as param>${param.getMCType().printType()} ${prefixes.parameter()}${param.getName()}<#sep>, </#items>
  </#list><#list helper.getFeatures(ast)>,
  <#items as feature>Boolean ${prefixes.feature()}${feature.getName()}<#sep>, </#items>
  </#list>
) {
  this.name = name;
  <#list ast.getHead().getArcParameterList() as param>
    this.${prefixes.parameter()}${param.getName()} = ${prefixes.parameter()}${param.getName()};
  </#list>
  <#list helper.getFeatures(ast) as feature>
    this.${prefixes.feature()}${feature.getName()} = ${prefixes.feature()}${feature.getName()};
  </#list>
  <@MethodNames.portSetup/>();
  <#if ast.getSymbol().isAtomic()>
  <@MethodNames.behaviorSetup/>();
  <#elseif ast.getSymbol().isDecomposed()>
  <@MethodNames.subCompSetup/>();
  <@MethodNames.connectorSetup/>();
  </#if>
}