<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#if ast.isPresentStereotype() && ast.getStereotype().contains("test")>

  ${tc.include("montiarc.generator.ma2jsim.unit.TestContext.ftl")}

</#if>