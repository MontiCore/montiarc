<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign compute = helper.getComputeBehavior(ast).get()/>

public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.compute()}<#if isTop>TOP</#if> extends
montiarc.rte.behavior.AbstractBehavior${"<"}${ast.getName()}${suffixes.context()}${">"} {

  protected ${ast.getName()}${suffixes.compute()}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.context()} ${ast.getName()?uncap_first}${suffixes.context()}) {
  super(${ast.getName()?uncap_first}${suffixes.context()});
  }

  public void init() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#if helper.getComputeInit(ast).isPresent()>
    ${prettyPrinter.prettyprint(helper.getComputeInit(ast).get().getMCBlockStatement())}
  </#if>
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendShadowedOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  }

  @Override
  public void tick() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [ast.getSymbol().getAllIncomingPorts(), true])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  ${prettyPrinter.prettyprint(compute.getMCBlockStatement())}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendShadowedOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  }

}