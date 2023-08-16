<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.Import.ftl", ast.getImportStatementList())}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.ContextInterface.ftl", ast.getComponentType(), helper.asList(isTop))}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.InputInterface.ftl", ast.getComponentType(), helper.asList(isTop))}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.OutputInterface.ftl", ast.getComponentType(), helper.asList(isTop))}