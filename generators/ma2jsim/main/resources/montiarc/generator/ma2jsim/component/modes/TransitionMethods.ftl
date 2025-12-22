<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign modeAutomaton = helper.getComponentHelper().getModeAutomaton(ast).get()>

<#list helper.getModeHelper().getTransitions(modeAutomaton) as transition>

  <#assign transitionIndex = helper.getModeHelper().getTransitionIndex(transition, modeAutomaton)>
  /** Transition action for: ${helper.getBehaviorHelper().printTransitionSignature(transition)} */
  protected void transition_${transitionIndex}() {
    <@logTransition transition/>

    this.currentMode = Mode.${transition.getTarget().getName()};
    this.context.<@MethodNames.modeTeardown transition.getSource().getNameSymbol()/>();
    this.context.<@MethodNames.modeSetup transition.getTarget().getNameSymbol()/>();
    this.context.<@MethodNames.modeInit transition.getTarget().getNameSymbol()/>();
  }
</#list>

<#macro logTransition transition>
  <#assign source = transition.getSource().getNameSymbol().getAstNode()>
  <#assign target = transition.getTarget().getNameSymbol().getAstNode()>
  <#assign removedSubs = helper.getModeHelper().getInstancesFromMode(source)>
  <#assign removedConnectors = helper.getModeHelper().getConnectors(source)>
  <#assign addedSubs = helper.getModeHelper().getInstancesFromMode(target)>
  <#assign addedConnectors = helper.getModeHelper().getConnectors(target)>

  <@Log.info log_aspects.modeChange() "this.compName">
    "${source.getName()} -> ${target.getName()};"

    <#if removedSubs?size != 0 || removedConnectors?size != 0>
      + " Removing"
      <#if removedSubs?size != 0>
        + " subs = {<#list removedSubs as s>${s.getName()}<#sep>, </#list>}"
      </#if>
      <#if removedConnectors?size != 0>
        + " connectors = {<#list removedConnectors as c>${c.getSourceName()} -> <@ModeUtil.formatStringList c.getTargetsNames()/><#sep>, </#list>}"
      </#if>
      + ";"
    </#if>
    <#if addedSubs?size != 0 || addedConnectors?size != 0>
      + <@ModeUtil.printAddedModeElements addedSubs addedConnectors/>
    </#if>
  </@Log.info>
</#macro>
