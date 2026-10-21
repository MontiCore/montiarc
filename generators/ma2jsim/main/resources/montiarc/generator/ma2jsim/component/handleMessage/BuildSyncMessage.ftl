<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign SYNC_MSG>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>

@Override
protected ${SYNC_MSG} buildSyncMessage() {
  return doBuildSyncMessage();
}

<#assign syncedPorts = helper.getComponentHelper().getSyncedInPortsOf(ast.getSymbol())>
protected ${SYNC_MSG} doBuildSyncMessage() {
  return new ${SYNC_MSG}(
    <#list ast.getSymbol().getAllIncomingPorts() as inPort>
      <#if syncedPorts?seq_contains(inPort)>
        ${prefixes.portValueOf()}${inPort.getName()}()
      <#else>
        ${helper.getTypeHelper().getNarrowedNullLikeValue(inPort.getType())}
      </#if>
    <#sep>, </#sep>
    </#list>
  );
}

