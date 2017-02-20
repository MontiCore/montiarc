package de.montiarcautomaton.ajava.generator

info("--------------------------------")
info("AJava Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("AJava Generator END")
info("--------------------------------")