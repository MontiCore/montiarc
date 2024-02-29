<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "isTop")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=ast.getComponentType()/>

public class ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if> {

  public static void main(String[] args) {
    final ${comp.getName()}${suffixes.component()} DEPLOY_${comp.getName()} =
      new ${comp.getName()}${suffixes.component()}${suffixes.builder()}("DEPLOY_${comp.getName()}").build();

    DEPLOY_${comp.getName()}.init();

    for(int cycles = 0; cycles < 20; cycles++) {
      DEPLOY_${comp.getName()}.<@MethodNames.handleTick/>();
    }
  }
}
