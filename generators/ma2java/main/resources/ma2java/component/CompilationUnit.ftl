<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("ma2java.Package.ftl", ast.getPackage())}
</#if>

${tc.include("ma2java.Import.ftl", ast.getImportStatementList())}

${tc.includeArgs("ma2java.component.Component.ftl", ast.getComponentType(), compHelper.asList(isTop))}
