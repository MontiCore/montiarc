<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="montiarc._ast.ASTMACompilationUnit" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- @ftlvariable name="isTop" type="boolean" -->

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.ma2jsim.component.interface.PublicApi.ftl", ast.getArcComponentType())}