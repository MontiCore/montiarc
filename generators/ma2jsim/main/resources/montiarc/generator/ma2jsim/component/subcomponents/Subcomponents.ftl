<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#list ast.getSubComponents() as astComponentInstantiation>
  ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentField.ftl", astComponentInstantiation)}

  ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentGetter.ftl", astComponentInstantiation)}
</#list>

protected void <@MethodNames.subCompSetup/>() {
<#list ast.getSubComponents() as astComponentInstantiation>
  ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentSetup.ftl", astComponentInstantiation)}
</#list>

<#if helper.getModeAutomaton(ast).isPresent()>
  <@MethodNames.dynSubCompSetup/>();
</#if>
}