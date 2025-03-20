<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#list ast.getHead().getArcParameterList() as param>
  ${tc.include("montiarc.generator.ma2jsim.component.parameters.ParameterField.ftl", param)}

  ${tc.include("montiarc.generator.ma2jsim.component.parameters.ParameterGetter.ftl", param)}
</#list>