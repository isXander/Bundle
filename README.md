# Bundle Updater

Update your mods automatically upon launch.

## How does it work?

This mod is actually quite simple! To summarise, it does the following:

- Scans the folder `.minecraft/bundle-mods` for mods.
- Uses the modrinth API to find the latest version of each mod.
- Downloads the latest version of each mod.
- Removes the old version of each mod.
- Instructs Quilt Loader to use `bundle-mods` as a mod folder.

## How do I install it?

First, **make sure you are using the latest version of Quilt Loader**. This mod will not work
with Fabric or Forge at this time.

### Installer

1. Download the installer from [my website](https://www.isxander.dev/bundle-updater)
2. Open the installer
3. Select the `.minecraft` folder you want to install it to.
4. Click install.

Once installed, any new mods you want to install should go to `.minecraft/bundle-mods`, not `.minecraft/mods`.

### Manual

1. Download the latest version of the mod from [my website](https://www.isxander.dev/bundle-updater),
   or from [Modrinth](https://modrinth.com/mod/bundle-updater) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bundle-updater).
2. Rename `.minecraft/mods` to `.minecraft/bundle-mods`
3. Create a new folder called `mods`, and place the bundle updater jar inside.
4. You're done!