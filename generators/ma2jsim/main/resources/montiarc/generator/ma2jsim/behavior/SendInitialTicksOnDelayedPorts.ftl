<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("portSymbols")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list portSymbols as port>
  <#if port.isStronglyCausal()>
    context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().<@MethodNames.sendTick/>();
  </#if>
</#list>
