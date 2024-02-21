<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>
    if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)} &&</#if> receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().getQualifiedName()))
      handlePortForward((montiarc.rte.port.TimeAwarePortForward<?>) ${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}());
    <#sep> else </#sep>
</#list>
<#list ast.getSymbol().getAllOutgoingPorts() as outPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, outPort)/>
    if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)} &&</#if> receivingPort.getQualifiedName().equals(${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)}().getQualifiedName()))
      ((montiarc.rte.port.TimeAwarePortForward${"<?>"}) ${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)}()).forward();
    <#sep> else </#sep>
</#list>