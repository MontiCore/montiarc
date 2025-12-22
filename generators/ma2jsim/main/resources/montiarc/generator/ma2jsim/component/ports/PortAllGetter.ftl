<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign hasOnlyOneVariant = helper.getVariantHelper().getVariants(ast)?size == 1>

<@allInPortsGetter/>
<@allOutPortsGetter/>
<@allSyncedInPortsGetter/>
<@allMsgEventInPortsGetter/>

<#macro allInPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(
      <#list ast.getSymbol().getAllIncomingPorts() as port>
        this.${prefixes.port()}${port.getName()}<#sep>,
      </#list>
    );
  <#else>
    switch (this.variantID) {
      <#list helper.getVariantHelper().getVariants(ast) as variant>
        case ${helper.getVariantHelper().variantSuffix(variant)}:
          return java.util.List.of(
            <#list variant.getAllIncomingPorts() as port>
              this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)}<#sep>,
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
      <#list helper.getVariantHelper().getVariants(ast) as variant>
        case ${helper.getVariantHelper().variantSuffix(variant)}:
        return java.util.List.of(
          <#list variant.getAllOutgoingPorts() as port>
            this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)}() <#sep>, </#sep>
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
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllSyncedInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(
    <#list helper.getComponentHelper().getSyncedInPortsOf(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}<#sep>,
    </#list>
    );
  <#else>
    switch (this.variantID) {
      <#list helper.getVariantHelper().getVariants(ast) as variant>
        case ${helper.getVariantHelper().variantSuffix(variant)}:
          return java.util.List.of(
            <#list helper.getComponentHelper().getSyncedInPortsOf(variant) as port>
              this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)}<#sep>,
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
@Override
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllMsgEventInPorts() {
  <#if hasOnlyOneVariant>
    return java.util.List.of(
    <#list helper.getComponentHelper().getMsgEventInPortsOf(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}<#sep>, </#sep>
    </#list>
    );

  <#else>
    switch (this.variantID) {
      <#list helper.getVariantHelper().getVariants(ast) as variant>
        case ${helper.getVariantHelper().variantSuffix(variant)}:
          return java.util.List.of(
            <#list helper.getComponentHelper().getMsgEventInPortsOf(variant) as port>
              this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)} <#sep>, </#sep>
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
