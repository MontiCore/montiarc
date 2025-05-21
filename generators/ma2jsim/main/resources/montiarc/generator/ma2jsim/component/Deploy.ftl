<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=variant!ast.getArcComponentType().getSymbol()/>
<#-- @ftlvariable name="comp" type=" arcbasis._symboltable.ArcComponentTypeSymbol" -->

public class ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.Deployment<${comp.getName()}${suffixes.comp()}> {

  public static void main(String[] args){
    new ${prefixes.deploy()}${comp.getName()}().deploy(args);
  }

  @Override
  public ${comp.getName()}${suffixes.comp()} buildComponent() {
    return new ${comp.getName()}${suffixes.comp()}${suffixes.builder()}("${comp.getName()}")
      <#if variant??>
        <#list variant.getFeatureSymbolBooleanMap() as feature, value>
      .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
      </#if>
    .build();
  }
}
