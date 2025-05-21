<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list ast.getSymbol().getSubcomponents() as subcomponent>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentField.ftl", [subcomponent, ""])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentGetter.ftl", [subcomponent, ""])}
</#list>

${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentAllGetter.ftl")}
${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentSetup.ftl")}