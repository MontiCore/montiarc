<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Header.ftl", ast, [isTop])}
{
<#assign classNameNoGenerics>${ast.getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign classNameWithGenerics>${classNameNoGenerics}<@Util.printTypeParameters ast false/></#assign>
${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/NameProperty.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/schedule/SchedulerInCompBuilder.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Constructors.ftl", ast, [classNameNoGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Parameters.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Features.ftl", ast, [classNameWithGenerics])}

${tc.include("montiarc/generator/ma2jsim/component/builder/Validation.ftl", ast)}

${tc.include("montiarc/generator/ma2jsim/component/builder/Build.ftl", ast)}
}