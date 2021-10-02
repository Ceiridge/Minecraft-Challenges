# Minecraft-Challenges
A Minecraft 1.15 Spigot plugin assisting in hosting challenges.\
(This project was quickly made for personal use, so it contains uncommented code without a git history. Also, the command syntax may be confusing.)

## Features
- Multiple challenges at the same time
- Beautiful and automated explanation of the challenge when starting
- Automated win condition checking
- Teams
- Challenge Selection Presets

## Supported Challenges
- Position Swap (Swapping player's position an an interval)
- Block Shuffle
- Juggernaut Manhunt
- Random Drops
- Random Crafting Recipes

### Challenge Modifiers
- Fast Mode
- Deathmatch
- Main Win Condition
- Team Win Condition
- PvE Win Condition

## Commands
- `/challenge <Name> <enable/disable/set> [<Setting Name>] [<Setting Value>]` (Load a challenge config)
- `/challengepreset <Name>` (Execute a challenge command preset)
- `/start [<ExplanationWaitSec>]` (Start the challenges)
- `/reset` (Reset the challenge server)
- `/givecomp <Player>` (Give tracking compass to a player)
- `/spawn`, `/hub` (Teleport to the lobby spawn)
