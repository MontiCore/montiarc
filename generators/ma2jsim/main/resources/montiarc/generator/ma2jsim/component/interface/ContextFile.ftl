<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTMACompilationUnit ast -->
${tc.signature("ast", "isTop")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.ContextInterface.ftl", ast.getComponentType(), [isTop])}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.InputInterface.ftl", ast.getComponentType(), [isTop])}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.OutputInterface.ftl", ast.getComponentType(), [isTop])}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.ParameterInterface.ftl", ast.getComponentType(), [isTop])}

${tc.includeArgs("montiarc.generator.ma2jsim.component.interface.FieldInterface.ftl", ast.getComponentType(), [isTop])}
