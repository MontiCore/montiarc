<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

public static class ${ast.getName()}TestContext implements montiarc.maunit.api.MaUnitTestContext {

  @Override
  public int testCount() {
    return ${MaUnitHelper.unitTestCaseCount(ast)};
  }

  @Override
  public String getDisplayName(int testIndex) {
    return "${ast.getName()}:" + testIndex;
  }

  @Override
  public int getTickCount(int testIndex) {
    <@returnStereoValue "ticks" 1?c/>
  }

  @Override
  public Object resolveParameter(int testIndex, int parameterIndex) {
    // constructor parameter index 0 (name) and 1 (scheduler) are outside of this context's control
    switch (parameterIndex) {
    <#list ast.getHead().getArcParameterList() as param>
      case ${param?index+2}: // ${param.getName()}
        <#if param.isPresentDefault()><#assign default = prettyPrinter.prettyprint(param.getDefault())></#if>
        <#if MaUnitHelper.isTestSource(ast)><@returnTestValue param?index default/><#else><@returnStereoValue param.getName() default/></#if>
    </#list>
    <#list helper.getFeatures(ast) as feature>
      case ${feature?index+2+ast.getHead().getArcParameterList()?size}: // ${feature.getName()}
        <@returnStereoValue feature.getName() false?c/>
    </#list>
    }
    throw new montiarc.maunit.api.ParameterResolutionException();
  }

  @Override
  public Class<?> getExpectedException(int testIndex) {
    <#assign StereoValue = MaUnitHelper.getStereoValue(ast, "exception")>
    <#if StereoValue.isPresent() && MaUnitHelper.isStereoValueList(StereoValue.get())>
    switch (testIndex) {
      <#list StereoValue.get().getExpression().getSetCollectionItemList() as item>
        case ${item?index}:
          return ${prettyPrinter.prettyprint(item.getExpression())};
      </#list>
    }
    throw new montiarc.maunit.api.ParameterResolutionException();
    <#elseif StereoValue.isPresent()>
    return ${prettyPrinter.prettyprint(StereoValue.get())};
    <#else>
    return null;
    </#if>
  }
}

<#macro returnStereoValue name default="">
  <#assign StereoValue = MaUnitHelper.getStereoValue(ast, name)>
  <#if StereoValue.isPresent()>
      return ${prettyPrinter.prettyprint(StereoValue.get().getExpression())}<#if MaUnitHelper.isStereoValueList(StereoValue.get())>.get(testIndex)</#if>;
  <#elseif default?has_content>
      return ${default};
  </#if>
</#macro>

<#macro returnTestValue parameterIndex default="">
  switch(testIndex) {
  <#list MaUnitHelper.getTestValues(ast, parameterIndex) as expression>
    case ${expression?index}:
      return ${prettyPrinter.prettyprint(expression)};
  </#list>
  <#if default?has_content>
    default: return ${default};
  </#if>
  }
</#macro>
