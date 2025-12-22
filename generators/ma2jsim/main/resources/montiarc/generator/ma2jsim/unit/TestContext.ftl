<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public static class ${ast.getName()}TestContext implements montiarc.maunit.api.MaUnitTestContext {

  @Override
  public int testCount() {
    return ${helper.getMaUnitHelper().unitTestCaseCount(ast)};
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
  public long getSimulatedTickLength(int testIndex) {
    <@returnStereoValue "simulatedTickLength" 0?c/>
  }

  @Override
  public Object resolveParameter(int testIndex, int parameterIndex) {
    switch (parameterIndex) {
    <#list ast.getHead().getArcParameterList() as param>
      case ${param?index}: // ${param.getName()}
        <#if param.isPresentDefault()><#assign default = prettyPrinter.prettyprint(param.getDefault())></#if>
        <#if helper.getMaUnitHelper().isTestSource(ast)><@returnTestValue param?index default/><#else><@returnStereoValue param.getName() default/></#if>
    </#list>
    <#list helper.getComponentHelper().getFeatures(ast) as feature>
      case ${feature?index+ast.getHead().getArcParameterList()?size}: // ${feature.getName()}
        <@returnStereoValue feature.getName() false?c/>
    </#list>
    }
    throw new montiarc.maunit.api.ParameterResolutionException();
  }

  @Override
  public Class<?> getExpectedException(int testIndex) {
    <#assign StereoValue = helper.getMaUnitHelper().getStereoValue(ast, "exception")>
    <#if StereoValue.isPresent() && helper.getMaUnitHelper().isStereoValueList(StereoValue.get())>
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
  <#assign StereoValue = helper.getMaUnitHelper().getStereoValue(ast, name)>
  <#if StereoValue.isPresent()>
      return ${prettyPrinter.prettyprint(StereoValue.get().getExpression())}<#if helper.getMaUnitHelper().isStereoValueList(StereoValue.get())>.get(testIndex)</#if>;
  <#elseif default?has_content>
      return ${default};
  </#if>
</#macro>

<#macro returnTestValue parameterIndex default="">
  switch(testIndex) {
  <#list helper.getMaUnitHelper().getTestValues(ast, parameterIndex) as expression>
    case ${expression?index}:
      return ${prettyPrinter.prettyprint(expression)};
  </#list>
  <#if default?has_content>
    default: return ${default};
  </#if>
  }
</#macro>
