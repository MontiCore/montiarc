<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#list ast.getFields() as astArcField>
  ${tc.include("montiarc.generator.ma2jsim.component.fields.FieldField.ftl", astArcField)}

  ${tc.include("montiarc.generator.ma2jsim.component.fields.FieldSetter.ftl", astArcField)}

  ${tc.include("montiarc.generator.ma2jsim.component.fields.FieldGetter.ftl", astArcField)}
</#list>
