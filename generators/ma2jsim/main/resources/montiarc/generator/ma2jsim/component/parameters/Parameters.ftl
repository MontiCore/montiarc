<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#list ast.getHead().getArcParameterList() as param>
  ${tc.include("montiarc.generator.ma2jsim.component.parameters.ParameterField.ftl", param)}

  ${tc.include("montiarc.generator.ma2jsim.component.parameters.ParameterGetter.ftl", param)}
</#list>