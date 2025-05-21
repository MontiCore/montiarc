<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
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
  protected void <@MethodNames.behaviorSetup/>${helper.variantSuffix(variant)}() {
    <#if hasAutomaton>
      this.behavior = new ${ast.getName()}${suffixes.automaton()}${helper.variantSuffix(variant)}${suffixes.builder()}<@Util.printTypeParameters ast false/>(this)
      .addDefaultStates()
      .setDefaultInitial()
      .setName(this.getName())
      .build();
    <#elseif hasCompute>
      this.behavior = new ${ast.getName()}${suffixes.compute()}${helper.variantSuffix(variant)}<@Util.printTypeParameters ast false/>(this, this.getName());
    </#if>
  }
</#if>
</#list>