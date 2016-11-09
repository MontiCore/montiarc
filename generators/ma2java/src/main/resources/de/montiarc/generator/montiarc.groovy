package de.montiarc.generator

// M1: configuration object "_configuration" prepared externally
info("--------------------------------")
info("MontiArc Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")

generate(modelPath, out);

info("--------------------------------")
info("MontiArc Generator END")
info("--------------------------------")
