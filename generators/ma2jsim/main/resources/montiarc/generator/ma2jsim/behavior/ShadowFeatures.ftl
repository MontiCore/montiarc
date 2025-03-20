<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("features")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list features as feature>
   final boolean ${feature.getName()} =
  context.${prefixes.feature()}${feature.getName()}();
</#list>