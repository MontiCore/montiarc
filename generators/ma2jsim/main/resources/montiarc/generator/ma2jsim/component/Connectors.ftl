<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariants(ast) as variant>
<#if !variant.isAtomic()>
protected void <@MethodNames.connectorSetup/>${helper.variantSuffix(variant)}() {
<#list variant.getAstNode().getConnectors() as conn>
    <#list conn.getTargetList() as target>
        <@portAccessorOf conn.getSource()/>.connect(<@portAccessorOf target/>);
    </#list>
</#list>

<#list variant.getSubcomponents() as subcomp>
  this.tickPort.connect(${prefixes.subcomp()}${subcomp.getName()}${helper.subcomponentVariantSuffix(ast, subcomp)}().getTickPort());
</#list>
}
</#if>
</#list>

<#-- connectorEndPoint may be the source of a connector, or one of its targets -->
<#macro portAccessorOf connectorEndPoint>
  <#assign IsSubcompTheOwner = connectorEndPoint.isPresentComponent()>

  <#assign variantSuffix = IsSubcompTheOwner?then(
    helper.portVariantSuffix(connectorEndPoint.getComponentSymbol(), connectorEndPoint.getPortSymbol()),
    helper.portVariantSuffix(ast, connectorEndPoint.getPortSymbol())
  )>

  <#if IsSubcompTheOwner>
    <#assign owningComp = connectorEndPoint.getComponentSymbol()>
    <#assign compAccessor = prefixes.subcomp() + connectorEndPoint.getComponent() + helper.subcomponentVariantSuffix(ast, owningComp) + "()">
    ${compAccessor}.${prefixes.port()}${connectorEndPoint.getPort()}${variantSuffix}()

  <#else>
    <#-- We directly access the field because we need to know that the port is an InOutPort -->
    ${prefixes.port()}${connectorEndPoint.getPort()}${variantSuffix}

  </#if>
</#macro>
