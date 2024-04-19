<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modeAutomatonClass>${ast.getName()}${suffixes.modeAutomaton()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign modeContextType>${ast.getName()}${suffixes.contextForModes()} <@Util.printTypeParameters ast/></#assign>

<#if isTop>abstract</#if> class ${modeAutomatonClass} <@Util.printTypeParameters ast/> {

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

  void init() {
    this.context.<@MethodNames.modeInit initMode.getSymbol()/>();
  }

  <#if helper.isEventBased(modeAutomaton)>
    ${tc.include("montiarc.generator.ma2jsim.component.modes.TickBehavior.ftl")}
    ${tc.include("montiarc.generator.ma2jsim.component.modes.MessageBehavior.ftl")}
  <#else>
    ${tc.include("montiarc.generator.ma2jsim.component.modes.TickBehavior.ftl")}
  </#if>
}