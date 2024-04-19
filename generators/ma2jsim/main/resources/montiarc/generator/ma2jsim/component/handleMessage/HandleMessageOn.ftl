<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)>
<#assign port>${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}()</#assign>

protected void <@MethodNames.handleBufferImplementation portSym/>${helper.portVariantSuffix(ast, portSym)}() {
  if (${port}.isBufferEmpty()) {
    return;
  }

  if (<@MethodNames.inputsTickBuffered/>()) {
    this.scheduleTick();
  } else  {
    this.schedule(this::handleMessageExecutionOn${portSym.getName()?cap_first}${helper.portVariantSuffix(ast, portSym)});
  }
}

protected void handleMessageExecutionOn${portSym.getName()?cap_first}${helper.portVariantSuffix(ast, portSym)}() {
  if (${port}.isBufferEmpty() || ${port}.isTickBlocked()) {
    return;
  }

  if ((isAtomic && isSync) <#if modeAutomatonOpt.isPresent()>|| isModeAutomatonSync</#if>) {
    return;  // Wait for ticks on all ports until processing data
  } else {
    // For event behavior, we can directly process the message
    <#assign msgMethodName>${prefixes.message()}${portSym.getName()}</#assign>
    <#if modeAutomatonOpt.isPresent() && helper.isEventBased(modeAutomatonOpt.get())>
      modeAutomaton.${msgMethodName}();
    </#if>
    if (this.isAtomic) {
      if (<@MethodNames.getBehavior/>() != null) {
        ((${ast.getName()}${suffixes.events()}) <@MethodNames.getBehavior/>()).${msgMethodName}${helper.portVariantSuffix(ast, portSym)}();
      } else { ${port}.pollBuffer(); }
    } else {  // The component is not atomic
      ((montiarc.rte.port.TimeAwarePortForComposition<?>) ${port}).forward();
    }
  }
}
