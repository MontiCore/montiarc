package de.montiarcautomaton.generator

info("--------------------------------")
info("MAA Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("MAA Generator END")
info("--------------------------------")