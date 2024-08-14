<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#assign SYNC_MSG = ast.getName() + suffixes.syncMsg()/>

@Override
protected ${SYNC_MSG} buildSyncMessage() {
  <#list helper.getVariants(ast) as variant>
  if (this.variantID.equals("${helper.variantSuffix(variant)}")) {
    return doBuildSyncMessage${helper.variantSuffix(variant)}();
  }
  <#sep> else </#sep>
  </#list>
  else {
    throw new java.lang.IllegalStateException("Component ${ast.getName()} is not correctly configured, no variant selected");
  }
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
            ${helper.getNullLikeValue(inPort.getType())}
          </#if>
        <#sep>, </#sep>
        </#list>
    );
  }
</#list>

