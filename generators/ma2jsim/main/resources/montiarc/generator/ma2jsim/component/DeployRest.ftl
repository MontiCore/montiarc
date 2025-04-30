<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=variant!ast.getComponentType().getSymbol()/>
<#-- @ftlvariable name="comp" type=" arcbasis._symboltable.ComponentTypeSymbol" -->

public class ${prefixes.deploy()}Rest${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.RestDeployment<${comp.getName()}${suffixes.component()}> {

  public static void main(String[] args){
    new ${prefixes.deploy()}Rest${comp.getName()}().deploy(args);
  }

  @Override
  public ${comp.getName()}${suffixes.component()} buildComponent() {
    return new ${comp.getName()}${suffixes.component()}${suffixes.builder()}("${comp.getName()}")
      <#if variant??>
        <#list variant.getFeatureSymbolBooleanMap() as feature, value>
      .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
        </#list>
      </#if>
    .build();
  }

  @Override
  protected void connect(${comp.getName()}${suffixes.component()} component, montiarc.rte.deploy.rest.SimpleRest server) {
    <#list comp.getAllIncomingPorts() as port>
      <#if !port.getType().isGenericType()>
      server.subscribe("/${comp.getName()}/${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}", str -> deserialize(str, <@Util.getPortTypeString port.getType()/>.class)
        .map(m -> {
          component.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(comp.getAstNode(), port)}().receive(montiarc.rte.msg.Message.of(m));
          return true;
        }).orElse(false)
      );
      </#if>
    </#list>
  }
}
