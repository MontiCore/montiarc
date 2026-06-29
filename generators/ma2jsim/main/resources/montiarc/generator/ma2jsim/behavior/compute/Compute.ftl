<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("variant")}
<#assign compute = helper.getBehaviorHelper().getComputeBehavior(ast).get() />
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.compute.Header.ftl", [compute])} {

  protected ${ast.getName()}${suffixes.compute()}${helper.getVariantHelper().variantSuffix(variant)}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.context()} ${ast.getName()?uncap_first}${suffixes.context()}, String name) {
  super(${ast.getName()?uncap_first}${suffixes.context()}, name);
  }

  @Override
  public void init() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  <#if helper.getBehaviorHelper().getComputeInit(ast).isPresent()>
    ${javaPrinter.generateCode(helper.getBehaviorHelper().getComputeInit(ast).get().getMCStatement())}
  </#if>

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
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", [helper.getComponentHelper().getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", [ast.getSymbol().getAllOutgoingPorts()])}
  ${javaPrinter.generateCode(compute.getMCStatement())}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", [ast.getFields()])}
  }

  <#-- ArcCompute does not support event driven behavior.
    -- The corresponding event messages must still be implemented (to obey the behavior signature).
    -- -> The methods are implemented throwing an exception.
    -->
  <#assign compSym = ast.getSymbol().getAdaptee()>
  <#list compSym.getAllIncomingPorts() as portSym>
    <#assign methodName = prefixes.message() + portSym.getName() + helper.getVariantHelper().portVariantSuffix(ast, portSym)>
    @Override
    public void ${methodName}(<@Util.getTypeString portSym.getType()/> msg) {
      de.se_rwth.commons.logging.Log.warn("The message cannot be handled by compute behavior and will be ignored");
    }
  </#list>

  ${tc.includeArgs("montiarc.generator.ma2jsim.behavior.IsDelayed.ftl", [compute])}
}
