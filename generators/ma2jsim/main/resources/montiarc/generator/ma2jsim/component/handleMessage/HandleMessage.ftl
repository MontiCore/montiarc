<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

@Override
public void handleMessage(montiarc.rte.port.IInPort<?> receivingPort) {
  ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>

    if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)} &&</#if> receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().getQualifiedName())) {
      <@MethodNames.handleBufferImplementation inPort/>${helper.portVariantSuffix(ast, inPort)}();
    } <#sep> else </#sep>
  </#list>

  // For output ports of compositions: forward the message
  if (!isAtomic) {
    <#list ast.getSymbol().getAllOutgoingPorts() as outPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, outPort)/>
      if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)} &&</#if> receivingPort.getQualifiedName().equals(${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)}().getQualifiedName())) {
        ((montiarc.rte.port.TimeAwarePortForComposition) ${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)}()).forward();
      } <#sep> else </#sep>
    </#list>
  }
}
