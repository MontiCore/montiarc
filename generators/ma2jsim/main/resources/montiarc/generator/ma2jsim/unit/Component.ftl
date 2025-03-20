<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#if ast.isPresentStereotype() && ast.getStereotype().contains("test")>

  ${tc.include("montiarc.generator.ma2jsim.unit.TestContext.ftl")}

</#if>