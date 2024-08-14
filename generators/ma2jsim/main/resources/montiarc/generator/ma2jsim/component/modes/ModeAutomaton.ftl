<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

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

  ${modeAutomatonClass}(${modeContextType} context) {
     this.context = context;
  }

  ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeEnum.ftl")}

  <#assign initMode = helper.getInitialModes(modeAutomaton)[0]>
  void setup() {
    // set initial state
    this.currentMode = Mode.${initMode.getName()};

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