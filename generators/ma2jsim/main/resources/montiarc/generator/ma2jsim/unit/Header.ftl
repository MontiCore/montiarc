<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#if ast.isPresentStereotype() && ast.getStereotype().contains("test")>
  @montiarc.maunit.api.MaUnitTest(${ast.getSymbol().getFullName()}${suffixes.component()}.${ast.getName()}TestContext.class)
</#if>