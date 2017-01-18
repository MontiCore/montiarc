package de.montiarcautomaton.lejosgenerator

info("--------------------------------")
info("LeJOS Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("LeJOS Generator END")
info("--------------------------------")