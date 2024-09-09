<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop" "variant")}
<#assign compute = helper.getComputeBehavior(ast).get() />
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.compute.Header.ftl", [isTop, compute])} {

  protected ${ast.getName()}${suffixes.compute()}${helper.variantSuffix(variant)}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.context()} ${ast.getName()?uncap_first}${suffixes.context()}, String name) {
  super(${ast.getName()?uncap_first}${suffixes.context()}, name);
  }

  @Override
  public void init() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#if helper.getComputeInit(ast).isPresent()>
    ${prettyPrinter.prettyprint(helper.getComputeInit(ast).get().getMCBlockStatement())}
  </#if>

  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  }

  <#assign syncMsgClass>${ast.getName()}${suffixes.syncMsg()} <@Util.printTypeParameters ast false/></#assign>
  <#assign inPorts = ast.getSymbol().getAllIncomingPorts()>

  @Override
  public void tick(${syncMsgClass} msg) {
    realTick(<#list inPorts as inPort> msg.${inPort.getName()}<#sep>,</#list>);
  }

  protected void realTick(
    <#list inPorts as inPort>
    <@Util.getTypeString inPort.getType()/> ${inPort.getName()}
    <#sep>,</#list>
  ) {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  ${prettyPrinter.prettyprint(compute.getMCBlockStatement())}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  }

  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/sync/UnsupportedEventBehaviorMembers.ftl", [true])}
}