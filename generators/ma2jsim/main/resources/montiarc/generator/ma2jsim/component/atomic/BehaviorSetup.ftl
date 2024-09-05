<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list helper.getVariants(ast) as variant>
<#if variant.isAtomic()>
    <#assign automaton = helper.getAutomatonBehavior(variant.getAstNode())/>
    <#assign hasAutomaton = automaton.isPresent()/>
    <#assign modeAutomaton = helper.getModeAutomaton(variant.getAstNode())>
    <#assign hasModeAutomaton = modeAutomaton.isPresent()>
    <#assign compute = helper.getComputeBehavior(variant.getAstNode())/>
    <#assign hasCompute = compute.isPresent()/>
    <#assign isEvent = (hasAutomaton && helper.isEventBased(automaton.get())) || (hasModeAutomaton && helper.isEventBased(modeAutomaton.get()))/>
  protected void <@MethodNames.behaviorSetup/>${helper.variantSuffix(variant)}() {
    this.isSync = ${(!isEvent)?c};
    <#if hasAutomaton>
      this.behavior = new ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(variant)}${suffixes.builder()}<@Util.printTypeParameters ast false/>(this)
      .addDefaultStates()
      .setDefaultInitial()
      .setName(this.getName())
      .build();
    <#elseif hasCompute>
      this.behavior = new ${ast.getName()}${suffixes.compute()}${helper.variantSuffix(variant)}<@Util.printTypeParameters ast false/>(this);
    </#if>
  }
</#if>
</#list>