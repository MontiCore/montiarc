<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#list ast.getSymbol().getFields() as fieldSym>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldField.ftl", [fieldSym])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldSetter.ftl", [fieldSym])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldGetter.ftl", [fieldSym])}
</#list>
