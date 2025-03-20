<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#list ast.getSymbol().getFields() as fieldSym>
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldField.ftl", [fieldSym])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldSetter.ftl", [fieldSym])}
  ${tc.includeArgs("montiarc.generator.ma2jsim.component.fields.FieldGetter.ftl", [fieldSym])}
</#list>
