<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#if ast.getSymbol().isAtomic()>
    <#assign automaton = helper.getBehaviorHelper().getAutomatonBehavior(ast)/>
    <#assign hasAutomaton = automaton.isPresent()/>
    <#assign modeAutomaton = helper.getComponentHelper().getModeAutomaton(ast)>
    <#assign hasModeAutomaton = modeAutomaton.isPresent()>
    <#assign compute = helper.getBehaviorHelper().getComputeBehavior(ast)/>
    <#assign hasCompute = compute.isPresent()/>
  protected void <@MethodNames.behaviorSetup/>() {
    <#if hasAutomaton>
      this.behavior = new ${ast.getName()}${suffixes.automaton()}${suffixes.builder()}<@Util.printTypeParameters ast false/>(this)
      .addDefaultStates()
      .setDefaultInitial()
      .setName(this.getName())
      .build();
    <#elseif hasCompute>
      this.behavior = new ${ast.getName()}${suffixes.compute()}<@Util.printTypeParameters ast false/>(this, this.getName());
    </#if>
  }
</#if>
