<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariants(ast) as variant>
<#assign atomic = variant.isAtomic()>
protected void <@MethodNames.portSetup/>${helper.variantSuffix(variant)}() {
${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign dynamicType><#if atomic>
        <@Util.getStaticPortClass inPort atomic/>
    <#else>
        montiarc.rte.port.TimeAwarePortForward
    </#if></#assign>

  this.${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)} = new ${dynamicType}<>(getName() + ".${inPort.getName()}", this);
</#list>

<#list ast.getSymbol().getAllOutgoingPorts() as outPort>
  this.${prefixes.port()}${outPort.getName()}${helper.portVariantSuffix(ast, outPort)} = new <@Util.getStaticPortClass outPort atomic/><>(getName() + ".${outPort.getName()}", this);
</#list>
}
</#list>