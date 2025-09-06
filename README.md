# HamUtils

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.9-blue)](https://www.minecraft.net/en-us)
[![Forge Version](https://img.shields.io/badge/Forge-11.15.1.2318-green)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html)

Utilities mod for Hammon on Hypixel.

---

## Features

- **Win rate tracker:** Tracks minutes per win to observe queue times
- **Win counter:** Increment and decrement wins with hotkeys (default `-` and `=` keys)
- **Auto /pr:** Automatically runs /pr on joining a parkour duels lobby
- **Foliage Hider**: Hides foliage for better visibility in parkour duels on Hypixel (especially for Atlantis)
- **Hide Players**: Hides other players in parkour duels for better visibility (toggleable with `z` key by default)

---

## Installation

1. Make sure you have **Minecraft 1.8.9** installed.
2. Install **Forge 1.8.9**: [Forge Downloads](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html).
3. Download the **Hypixel Mod Api 1.0.1.2**: [Hypixel Mod Api](https://modrinth.com/mod/hypixel-mod-api/versions).
4. Download the latest version of **HamUtils**: [HamUtils](https://github.com/Shadowmachete/HamUtils/releases).
5. Place both `.jar` files into your `mods` folder:
    - Windows: `%appdata%\.minecraft\mods`
    - macOS: `~/Library/Application Support/minecraft/mods`
    - Linux: `~/.minecraft/mods`
6. Launch Minecraft with the **Forge 1.8.9** profile.

---

## Usage

- Use `0` key to start / pause / continue timer
- Use `9` key to reset timer
- Use `-` key to decrement wins
- Use `=` key to increment wins
- Use `z` key to toggle player visibility
 
---

## Configuration

- Config file at `config/hamutils-data.json` in the Minecraft folder.
- Stores total time elapsed and number of wins

---

## Dependencies

- **Forge 1.8.9** (11.15.1.2318 or above)
- **Hypixel Mod Api** (1.0.1.2 for 1.8.9)

---

## License

HamUtils is licensed under **GNU General Public License v3.0**, see: [LICENSE](LICENSE).