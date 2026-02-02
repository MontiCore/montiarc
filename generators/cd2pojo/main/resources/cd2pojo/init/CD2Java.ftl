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

${glex.replaceTemplate("cd2java.EmptyBody", glex.templateHP("cd2pojo.EmptyBody.ftl"))}

${decConfig.withCopyCreator().defaultApply()}
${decConfig.withGetters().applyOnName("getter").ignoreOnName("noGetter")}
${decConfig.withDefaultsForCardinalityAttrs().applyOnName("defaultCardinality").ignoreOnName("noDefaultCardinality")}
${decConfig.withSetters().applyOnName("setter").ignoreOnName("noSetter")}
${decConfig.withNavigableSetters().applyOnName("setter").ignoreOnName("noSetter")}
${decConfig.withBuilders().applyOnName("builder").ignoreOnName("noBuilder")}
${decConfig.withObservers().applyOnName("observable").ignoreOnName("notObservable")}
