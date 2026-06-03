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
public class ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if>
  extends montiarc.rte.deploy.Deployment<${comp.getName()}${suffixes.comp()}> {

<#if comp.getPorts()?size == 0>
  public static void main(String[] args){
    new ${prefixes.deploy()}${comp.getName()}().deploy(args);
  }

  public ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if>() {
    super();
  }
</#if>

public ${prefixes.deploy()}${comp.getName()}<#if isTop>${suffixes.top()}</#if>(montiarc.rte.deploy.DeploymentStrategy<${comp.getName()}${suffixes.comp()}> strategy) {
  super(strategy);
}

@Override
protected ${comp.getName()}${suffixes.comp()} buildComponent(montiarc.rte.scheduling.CoordinatingScheduler scheduler, java.util.Map<String, String> parameters) {
  ${comp.getName()}${suffixes.comp()}${suffixes.builder()} builder = new ${comp.getName()}${suffixes.comp()}${suffixes.builder()}("${comp.getName()}")
    .setScheduler(scheduler)
<#if variant??>
  <#list variant.getFeatureSymbolBooleanMap() as feature, value>
    .${prefixes.setterMethod()}${prefixes.feature()}${feature.getName()}(${value?c})
  </#list>
</#if>
    ;

<#list comp.getParameterList() as param>
  if (parameters.containsKey("${param.getName()}")) {
      builder.${prefixes.setterMethod()}${prefixes.parameter()}${param.getName()}(
            deSerializer.deserialize(
            parameters.get("${param.getName()}"),
  <#if param.getType().isGenericType()>
            new tools.jackson.core.type.TypeReference<<@Util.getTypeString param.getType()/>>() {}
  <#else>
    <@Util.getTypeString param.getType()/>.class
  </#if>
      ).orElse(${helper.getTypeHelper().getNarrowedNullLikeValue(param.getType())})
    );
  }
</#list>

  return builder.build();
}

<#if comp.getParameterList()?size gt 0>
  @Override
  protected void addOptionsForParameters(org.apache.commons.cli.Options options) {
  <#list comp.getParameterList() as param>
    <#assign isRequired = !param.getAstNode().isPresentDefault()>
    org.apache.commons.cli.Option option_${param.getName()} = org.apache.commons.cli.Option.builder()
            .longOpt("${param.getName()}")
            .hasArg()
            .argName("${param.getName()}")
            .desc("Parameter ${param.getName()}, Type: ${param.getType().printFullName()}")
            .required(${isRequired?c})
            .get();
    options.addOption(option_${param.getName()});
  </#list>
}
</#if>
}
