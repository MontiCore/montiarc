<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=variant!ast.getComponentType().getSymbol()/>

public class ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if> {

  public static void main(String[] args) {
    final ${comp.getName()}${suffixes.component()} DEPLOY_${comp.getName()} =
      new ${comp.getName()}${suffixes.component()}${suffixes.builder()}("DEPLOY_${comp.getName()}")
      <#if variant??>
        <#list variant.getFeatureSymbolBooleanMap() as feature, value>
      .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
      </#if>
      .build();

    DEPLOY_${comp.getName()}.init();

    DEPLOY_${comp.getName()}.run();
  }
}
