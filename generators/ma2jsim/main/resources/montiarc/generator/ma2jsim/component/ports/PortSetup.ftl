<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign atomic = ast.getSymbol().isAtomic()/>
protected void <@MethodNames.portSetup/>() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign dynamicType><#if atomic>
        <@Util.getStaticPortClass inPort atomic/>
    <#else>
        montiarc.rte.port.TimeAwarePortForward
    </#if></#assign>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>
    <#if existenceConditions?has_content>
        if(${prettyPrinter.prettyprint(existenceConditions)}) {
    </#if>
  this.${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)} = new ${dynamicType}<>(getName() + ".${inPort.getName()}", this);
    <#if existenceConditions?has_content>
        }
    </#if>
</#list>

<#list ast.getSymbol().getAllOutgoingPorts() as outPort>
    <#assign dynamicType><#if !atomic>
        montiarc.rte.port.TimeAwarePortForward
    <#else>
        <@Util.getStaticPortClass outPort atomic/>
    </#if></#assign>
    <#assign existenceConditions = helper.getExistenceCondition(ast, outPort)/>
    <#if existenceConditions?has_content>
        if(${prettyPrinter.prettyprint(existenceConditions)}) {
    </#if>
  this.${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)} = new ${dynamicType}<>(getName() + ".${outPort.getName()}", this);
    <#if existenceConditions?has_content>
        }
    </#if>
</#list>
}