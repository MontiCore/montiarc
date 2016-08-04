${tc.params("String type", "String name")}

        get${name?cap_first}().send(sim.generic.Tick.<${type}> get());
