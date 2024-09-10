<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
${tc.signature("variant")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.automata.AutomatonBuilder.ftl", variant.getAstNode(), [variant])}