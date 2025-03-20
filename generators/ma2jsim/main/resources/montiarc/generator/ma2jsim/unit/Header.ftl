<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#if ast.isPresentStereotype() && ast.getStereotype().contains("test")>
  @montiarc.maunit.api.MaUnitTest(${ast.getSymbol().getFullName()}${suffixes.component()}.${ast.getName()}TestContext.class)
</#if>