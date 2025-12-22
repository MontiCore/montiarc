<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Assumed variables: ASTMACompilationUnit ast, boolean isTop -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
  ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>

<#assign comp=variant!ast.getArcComponentType().getSymbol()/>
<#-- @ftlvariable name="comp" type="de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol" -->

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
public class ${prefixes.deploy()}Rest${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.RestDeployment<${comp.getName()}${suffixes.comp()}> {

  public static void main(String[] args){
    new ${prefixes.deploy()}${comp.getName()}(new ${prefixes.deploy()}Rest${comp.getName()}()).deploy(args);
  }

  @Override
  protected void setupEndpoints(${comp.getName()}${suffixes.comp()} component) {
    <#list comp.getAllIncomingPorts() as port>
      <#if !port.getType().isGenericType()>
      server.subscribe("/${comp.getName()}/${port.getName()}${helper.getVariantHelper().portVariantSuffix(comp.getAstNode(), port)}", str -> deSerializer.deserialize(str, <@Util.getPortTypeString port.getType()/>.class)
        .map(m -> {
          component.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(comp.getAstNode(), port)}().receive(montiarc.rte.msg.Message.of(m));
          return true;
        }).orElse(false)
      );
      </#if>
    </#list>
  }
}
