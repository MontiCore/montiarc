<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariants(ast) as variant>
<#if !variant.isAtomic()>
protected void <@MethodNames.connectorSetup/>${helper.variantSuffix(variant)}() {
<#list variant.getAstNode().getConnectors() as conn>
    <#assign sourcePort>
        <#if conn.getSource().isPresentComponent()>${prefixes.subcomp()}${conn.getSource().getComponent()}${helper.subcomponentVariantSuffix(ast, conn.getSource().getComponentSymbol())}().
        <#else>(montiarc.rte.port.IOutPort)
        </#if>
        ${prefixes.port()}${conn.getSource().getPort()}<#if conn.getSource().isPresentComponent()>${helper.portVariantSuffix(conn.getSource().getComponentSymbol(), conn.getSource().getPortSymbol())}<#else>${helper.portVariantSuffix(ast, conn.getSource().getPortSymbol())}</#if>()
    </#assign>
    <#list conn.getTargetList() as target>
        <#assign targetPort>
            <#if target.isPresentComponent()>${prefixes.subcomp()}${target.getComponent()}${helper.subcomponentVariantSuffix(ast, target.getComponentSymbol())}().
            <#else>(montiarc.rte.port.IInPort)
            </#if>
            ${prefixes.port()}${target.getPort()}<#if target.isPresentComponent()>${helper.portVariantSuffix(target.getComponentSymbol(), target.getPortSymbol())}<#else>${helper.portVariantSuffix(ast, target.getPortSymbol())}</#if>()
        </#assign>
        (${sourcePort}).connect(${targetPort});
    </#list>
</#list>

<#list variant.getSubcomponents() as subcomp>
  this.getTickPort().connect(${prefixes.subcomp()}${subcomp.getName()}${helper.subcomponentVariantSuffix(ast, subcomp)}().getTickPort());
</#list>
}
</#if>
</#list>