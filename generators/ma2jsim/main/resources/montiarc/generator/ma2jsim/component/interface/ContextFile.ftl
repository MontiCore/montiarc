<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.ma2jsim.component.interface.ContextInterface.ftl", ast.getComponentType())}

${tc.include("montiarc.generator.ma2jsim.component.interface.OutputInterface.ftl", ast.getComponentType())}

${tc.include("montiarc.generator.ma2jsim.component.interface.ParameterInterface.ftl", ast.getComponentType())}

${tc.include("montiarc.generator.ma2jsim.component.interface.FieldInterface.ftl", ast.getComponentType())}

${tc.include("montiarc.generator.ma2jsim.component.interface.FeatureInterface.ftl", ast.getComponentType())}
