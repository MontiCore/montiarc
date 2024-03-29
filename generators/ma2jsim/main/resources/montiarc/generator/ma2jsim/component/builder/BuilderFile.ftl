<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.includeArgs("montiarc.generator.ma2jsim.component.builder.Builder.ftl", ast.getComponentType(), [isTop])}
