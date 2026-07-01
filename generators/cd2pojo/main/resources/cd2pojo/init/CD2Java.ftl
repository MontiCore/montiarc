<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Configuration template for MontiArc-compatible generation.

  If you want to use a custom configuration template (e.g. by specifying it with
  the '-ct' CLI argument), make sure it stays compatible with MontiArc, e.g., by
  applying the same decorators as this template does.
-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="decConfig" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="genSetup" type="de.monticore.generating.GeneratorSetup" -->

<#-- Use default configuration  -->
${tc.include("cd2java.init.CD2Pojo")}
${decConfig.withGetters().defaultIgnore()}
${decConfig.withDefaultsForCardinalityAttrs().defaultIgnore()}
${decConfig.withSetters().defaultIgnore()}
${decConfig.withNavigableSetters().defaultIgnore()}
${decConfig.withAbstractMethodSignatures().defaultIgnore()}

<#-- Replace default templates -->
${glex.replaceTemplate("cd2java.EmptyBody", glex.templateHP("cd2pojo.EmptyBody.ftl"))}
