<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modeAutomatonClass>${ast.getName()}${suffixes.modeAutomaton()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign modeContextType>${ast.getName()}${suffixes.contextForModes()} <@Util.printTypeParameters ast/></#assign>
<#assign implInterface>
    <#if helper.isEventBased(modeAutomaton)>EventModeAutomaton
    <#else>SyncModeAutomaton<${ast.getName()}${suffixes.syncMsg()}>
</#if></#assign>

<#if isTop>abstract</#if> class ${modeAutomatonClass} <@Util.printTypeParameters ast/>
  implements ${ast.getName()}${suffixes.events()} <@Util.printTypeParameters ast false/> {

  protected final ${modeContextType} context;

  protected Mode currentMode;

  protected String compName;

  ${modeAutomatonClass}(${modeContextType} context, String compName) {
     this.context = context;
     this.compName = compName;
  }

  ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeEnum.ftl")}

  <#assign initMode = helper.getInitialModes(modeAutomaton)[0]>
  void setup() {
    // set initial state
    this.currentMode = Mode.${initMode.getName()};
    <@logInitialMode initMode/>

    // instantiate sub components
    this.context.<@MethodNames.modeSetup initMode.getSymbol()/>();
  }

  public void init() {
    this.context.<@MethodNames.modeInit initMode.getSymbol()/>();
  }

  <#if helper.isEventBased(modeAutomaton)>
    ${tc.include("montiarc.generator.ma2jsim.component.modes.EventTickBehavior.ftl")}
    ${tc.include("montiarc.generator.ma2jsim.component.modes.MessageBehavior.ftl")}
  <#else>
    ${tc.include("montiarc.generator.ma2jsim.component.modes.SyncTickBehavior.ftl")}
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/sync/UnsupportedEventBehaviorMembers.ftl", [false])}
  </#if>
}

<#macro logInitialMode mode>
  <#assign addedSubs = helper.getInstancesFromMode(mode)>
  <#assign addedConnectors = helper.getConnectors(mode)>
  <@Log.info log_aspects.modeChange() "this.compName">
      "-> ${initMode.getName()};"
    <#if addedSubs?size != 0 || addedConnectors?size != 0>
      + <@ModeUtil.printAddedModeElements addedSubs addedConnectors/>
    </#if>
  </@Log.info>
</#macro>
