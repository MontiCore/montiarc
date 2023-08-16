<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

${tc.include("montiarc.generator.Import.ftl", ast.getImportStatementList())}

<#assign comp=ast.getComponentType()/>

public class ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if> {

  public static void main() {
    ${comp.getName()}${suffixes.component()} DEPLOY_${comp.getName()} =
      new ${comp.getName()}${suffixes.component()}${suffixes.builder()}("DEPLOY_${comp.getName()}").build();

    DEPLOY_${comp.getName()}.init();
  }
}
