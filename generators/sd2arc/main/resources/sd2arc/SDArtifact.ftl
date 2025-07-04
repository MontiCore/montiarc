<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackageDeclaration()>
package ${ast.getPackageDeclaration().getQName()};
</#if>

<#list ast.getMCImportStatementList() as import >
import ${import.getQName()}<#if import.isStar()>.*</#if>;
</#list>

${tc.include("sd2arc.Component.ftl", ast.getSequenceDiagram())}
