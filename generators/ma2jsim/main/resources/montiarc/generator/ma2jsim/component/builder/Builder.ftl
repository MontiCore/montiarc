<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.include("montiarc/generator/ma2jsim/component/builder/Header.ftl", ast)}
{
<#assign classNameNoGenerics>${ast.getName()}${suffixes.comp()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign classNameWithGenerics>${classNameNoGenerics}<@Util.printTypeParameters ast false/></#assign>
${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/NameProperty.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/SuperComponent.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/schedule/SchedulerInCompBuilder.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/OracleElements.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Constructors.ftl", ast, [classNameNoGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Parameters.ftl", ast, [classNameWithGenerics])}

${tc.includeArgs("montiarc/generator/ma2jsim/component/builder/Features.ftl", ast, [classNameWithGenerics])}

${tc.include("montiarc/generator/ma2jsim/component/builder/Validation.ftl", ast)}

${tc.include("montiarc/generator/ma2jsim/component/builder/Build.ftl", ast)}
}
