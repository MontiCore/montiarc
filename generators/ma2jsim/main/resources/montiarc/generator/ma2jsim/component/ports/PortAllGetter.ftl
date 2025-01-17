<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

<@allInPortsGetter/>
<@allOutPortsGetter/>
<@allSyncedInPortsGetter/>
<@allMsgEventInPortsGetter/>

<#macro allInPortsGetter>
@Override
protected java.util.List${"<"}montiarc.rte.port.InOutPort${"<?>>"} getAllInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(tickPort
      <#list ast.getSymbol().getAllIncomingPorts() as port>
        , this.${prefixes.port()}${port.getName()}
      </#list>
    );
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
          return java.util.List.of(tickPort
            <#list variant.getAllIncomingPorts() as port>
              , this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}
            </#list>
          );
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return java.util.Collections.emptyList();
    }
  </#if>
}
</#macro>



<#macro allOutPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.OutPort${"<?>>"} getAllOutPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(
      <#list ast.getSymbol().getAllOutgoingPorts() as port>
        this.${prefixes.port()}${port.getName()}() <#sep>, </#sep>
      </#list>
    );
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
        return java.util.List.of(
          <#list variant.getAllOutgoingPorts() as port>
            this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}() <#sep>, </#sep>
          </#list>
        );
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return java.util.Collections.emptyList();
    }
  </#if>
}
</#macro>



<#macro allSyncedInPortsGetter>
@Override
protected java.util.List${"<"}montiarc.rte.port.InOutPort${"<?>>"} getAllSyncedInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(tickPort
    <#list helper.getSyncedInPortsOf(ast.getSymbol()) as port>
      , this.${prefixes.port()}${port.getName()}
    </#list>
    );
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
          return java.util.List.of(tickPort
            <#list helper.getSyncedInPortsOf(variant) as port>
              , this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}
            </#list>
          );
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return java.util.Collections.emptyList();
    }
  </#if>
}
</#macro>



<#macro allMsgEventInPortsGetter>
protected java.util.List${"<"}montiarc.rte.port.InOutPort${"<?>>"} getAllMsgEventInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(
    <#list helper.getMsgEventInPortsOf(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}<#sep>, </#sep>
    </#list>
    );

  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
          return java.util.List.of(
            <#list helper.getMsgEventInPortsOf(variant) as port>
              this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)} <#sep>, </#sep>
            </#list>
          );
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return java.util.Collections.emptyList();
    }
  </#if>
}
</#macro>