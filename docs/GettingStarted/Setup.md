<!-- (c) https://github.com/MontiCore/monticore -->
# Setup MontiArc

MontiArc runs on any platform. In the following tutorials, we will use both the CLI and the Gradle plugin.

#### Gradle

If you only ever want to use the [Gradle plugin](./HelloGradle.md), the only thing you need is Java 21.
So if you haven't already, now is the right time to [install Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21).

#### Run the CLI Manually

To run the CLI manually, you need to have [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) installed.
Next, download the [MontiArc-7.9.0.jar](https://github.com/MontiCore/montiarc/releases/tag/snapshot).

You can then run the tool with:

```bash
java -jar MontiArc-7.9.0.jar
```

#### Install the CLI (BETA)

If you also want to use the [CLI](./HelloWorld.md), you can find installers for all platforms [here](https://github.com/MontiCore/montiarc/releases/tag/snapshot).

=== "Windows"
    1. Download the installer for Windows(.exe), extract, and run it.
    2. To be able to run the MontiArc command from anywhere, you will need to update your path variable:
        - Open the advanced system settings (Erweiterte System Eigenschaften) by pressing the Windows key and searching for it.
        - Press **Environment Variables** (Umgebungsvariablen)
        - Inside the user variables, look for `Path`
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
    1. Download the installer for MacOS(.pkg) 
    2. Run the installer and follow the instructions.
        - You will probably receive following message: ''Apple could not verify “MontiArc-[...].pkg” is free of malware that may harm your Mac or compromise your privacy.''
            1. Navigate to `System Settings`
            2. Navigate to `Privacy & Security`
            3. Navigate to `Security`
            4. Search for the message ''... was blocked from use because it is not from an identified developer''
            5. Click on **Open anyway**
    3. You are now able to use the MontiArc tool.
    4. To be able to run the MontiArc command from anywhere, you will need to update your path variable:
        - Add `export PATH="/Applications/MontiArc.app/Contents/MacOS:$PATH"` to your `~/.zshrc` file.
        - If that file doesn't exist, create it. 
        - Restart your shell or run `source ~/.zshrc` to load the changes.
  
    !!! info 
        MontiArc can only be used via the terminal. Clicking on the MontiArc app icon displayed in your app overview will not work.

=== "Linux"
    1. Download the installer for Linux(.deb/.rpm) and run it.
    2. To be able to run the MontiArc command from anywhere, you will need to update your path variable:
        - You should already know how to add a program to your path?
        - If not, simply add `export PATH="/opt/montiarc/bin:$PATH"` to whatever shell's rc script you're using (likely `~/.bashrc`).

You can then run the tool by running:
```bash
MontiArc
```

When you intend to run the simulation using the CLI, you'll need [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) anyway.

---

You can find detailed descriptions and available parameters in the [usage](../Usage/index.md) section.
