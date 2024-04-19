<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list ast.getSymbol().getSubcomponents() as subcomponent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentField.ftl", [subcomponent, ""])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentGetter.ftl", [subcomponent, ""])}
</#list>

${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentAllGetter.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentSetup.ftl")}