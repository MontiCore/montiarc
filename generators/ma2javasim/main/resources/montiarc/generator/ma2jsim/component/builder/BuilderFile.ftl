<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.Import.ftl", ast.getImportStatementList())}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Header.ftl", ast.getComponentType(), [isTop])}
{
<#assign className>${ast.getComponentType().getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if></#assign>
  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/NameProperty.ftl", ast.getComponentType(), [className])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Constructors.ftl", ast.getComponentType(), [className])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Parameters.ftl", ast.getComponentType(), [className])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Features.ftl", ast.getComponentType(), [className])}

  ${tc.include("montiarc/generator/ma2jsim/component/builder/Validation.ftl", ast.getComponentType())}

  ${tc.include("montiarc/generator/ma2jsim/component/builder/Build.ftl", ast.getComponentType())}
}
