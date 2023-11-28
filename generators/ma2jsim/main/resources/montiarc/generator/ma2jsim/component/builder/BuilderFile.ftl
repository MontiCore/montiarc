<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Header.ftl", ast.getComponentType(), [isTop])}
{
<#assign classNameNoGenerics>${ast.getComponentType().getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign classNameWithGenerics>${classNameNoGenerics}<@Util.printTypeParameters ast.getComponentType() false/></#assign>
  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/NameProperty.ftl", ast.getComponentType(), [classNameWithGenerics])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/schedule/SchedulerInCompBuilder.ftl", ast.getComponentType(), [classNameWithGenerics])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Constructors.ftl", ast.getComponentType(), [classNameNoGenerics])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Parameters.ftl", ast.getComponentType(), [classNameWithGenerics])}

  ${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Features.ftl", ast.getComponentType(), [classNameWithGenerics])}

  ${tc.include("montiarc/generator/ma2jsim/component/builder/Validation.ftl", ast.getComponentType())}

  ${tc.include("montiarc/generator/ma2jsim/component/builder/Build.ftl", ast.getComponentType())}
}
