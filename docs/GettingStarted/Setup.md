<!-- (c) https://github.com/MontiCore/monticore -->
# Setup MontiArc

MontiArc runs on any platform. In the following tutorials, we will use both the CLI and the Gradle plugin.

#### Gradle

If you only ever want to use the [Gradle plugin](./HelloGradle.md), the only thing you need is Java 11.
So if you haven't already, now is the right time to [install Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21).

#### Run the CLI Manually

To run the CLI manually, you need to have [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) installed.
Next, download the [MontiArc-7.9.0.jar](https://github.com/MontiCore/montiarc/releases/tag/snapshot).

You can then run the tool with:

```bash
java -jar MontiArc-7.9.0.jar
```

#### Install the CLI (BETA)

If you also want to use the [CLI](./HelloWorld.md), you can find installers for all platforms [here](https://github.com/MontiCore/montiarc/releases/tag/snapshot). Download the installer for your platform, extract, and run it.

!!! info "Add MontiArc to PATH"
    To be able to run the MontiArc command from anywhere, you will need to update your path variable.

    === "Windows"
        1. Open the advanced system settings (Erweiterte System Eigenschaften) by pressing the Windows key and searching for it.
        2. Press **Environment Variables** (Umgebungsvariablen)
        3. Inside the user variables, look for `Path`
            - If `Path` exists
                1. Select `Path` 
                2. Click **Edit...**
                3. Click **New...**
                4. Insert `C:\Program Files\MontiArc\`
                5. Click **OK** three times
            - If `Path` does not exist
                 1. Click **New...** in the user variables section
                 2. Set **Variable Name** to `Path`
                 3. Set **Variable Value** to `C:\Program Files\MontiArc\`
                 4. Click **OK** three times

    === "MacOS"
        Add `export PATH="/Applications/MontiArc/bin:$PATH"` to your `~/.zshrc` file.
        If that file doesn't exist, create it. Restart your shell or run `source ~/.zshrc` to load the changes.

    === "Linux"
        You should already know how to add a program to your path?
        
        If not, simply add `export PATH="/opt/montiarc/bin:$PATH"` to whatever shell's rc script you're using (likely `~/.bashrc`).


You can then run the tool by running:
```bash
MontiArc
```

When you intend to run the simulation using the CLI, you'll need [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) anyway.

---

You can find detailed descriptions and available parameters in the [usage](../Usage/index.md) section.
