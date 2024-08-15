<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign SYNC_MSG>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

@Override
protected ${SYNC_MSG} buildSyncMessage() {
  <#if hasOnlyOneVariant>
    return doBuildSyncMessage();
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
          return doBuildSyncMessage${helper.variantSuffix(variant)}();
      </#list>
      default:
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return new ${SYNC_MSG}(
          <#list ast.getSymbol().getAllIncomingPorts() as inPort>
            ${helper.getNarrowedNullLikeValue(inPort.getType())}
            <#sep>, </#sep>
          </#list>
      );
    }
  </#if>
}

<#list helper.getVariants(ast) as variant>
  <#assign variantPorts = variant.getAllIncomingArcPorts()>
  protected ${SYNC_MSG} doBuildSyncMessage${helper.variantSuffix(variant)}() {
    return new ${SYNC_MSG}(
        <#list ast.getSymbol().getAllIncomingPorts() as inPort>
          <#if variantPorts?seq_contains(inPort)>
            ${prefixes.portValueOf()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}()
          <#else>
            <#-- The SyncedInputs class is a 150% class. It contains fields for the ports of all possible variants
              -- of a component. Therefore, we have to call the constructor with values for non-existing ports, too
              -- (regarding the given variant).
              -->
            ${helper.getNarrowedNullLikeValue(inPort.getType())}
          </#if>
        <#sep>, </#sep>
        </#list>
    );
  }
</#list>

