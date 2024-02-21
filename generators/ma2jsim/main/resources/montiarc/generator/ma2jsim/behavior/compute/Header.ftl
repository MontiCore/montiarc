<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop", "compute")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.compute()}${helper.variantSuffix(ast.getSymbol())}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> extends
montiarc.rte.behavior.AbstractBehavior${"<"}${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/>${">"}