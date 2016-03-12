# Netrogue

#### An online roguelike with projectile spellcasting, (nearly) infinite dungeons, and lots of running away.

Or at least that was the idea 165 hours ago, when we started working on a networked dungeon crawler game for the [7DRL Challenge](7drl.org/about). Over 140 of those hours were spent designing the networking framework that the game used to synchronize objects over a network. In spite of fundamental architectural issues (mostly arising from memory-intensive [diff generation](https://github.com/AnimatedRNG/netrogue/blob/837dec0b31f242c68e36ad49a53dab3d10c2362c/core/src/main/animated/spferical/netrogue/networking/GameServer.java#L69)), it somehow works. And we have Netrogue -- an online, real-time roguelike.

![](https://raw.githubusercontent.com/AnimatedRNG/netrogue/05ee01dbb74c5155473772f9412d7a482b173e1a/images/screenshot1.png)

![enter image description here](https://raw.githubusercontent.com/AnimatedRNG/netrogue/05ee01dbb74c5155473772f9412d7a482b173e1a/images/screenshot2.png)

### Description
Netrogue is a cooperative-competitive multiplayer game. When you enter the game, you log in to our server, and a character is generated for you in the same world that is inhabited by other players. The dungeon is randomly generated and fairly large (although a couple orders of magnitude smaller than what we intended). Friendly fire is allowed on ranged attacks, although we suggest you focus on PVE rather than PVP ;)

As you defeat various monsters through the course of the the game, you will gain experience and level up. Leveling up allows you to specialize in either melee or magic. Melee attacks tend to be more powerful than magic attacks, but are limited in range. Magical attacks tend to have a larger range than melee attacks, but deal less damage.

### Installation

 1. Download the final build of Netrogue from Github Releases.
 2. Run the jar using the Java 8 JRE.
	 - *Double-click it, and if the game window doesn't pop up, [update Java](https://java.com/en/download/). If it still doesn't work, file an issue on the issue tracker with all relevant details (console output, etc)*

### Controls

| Description       | Key        |
|-------------------|------------|
| Move/Attack Left  | A          |
| Move/Attack Up    | W          |
| Move/Attack Down  | S          |
| Move/Attack Right | D          |
| Pick up item      | , *or* G   |
| Select slots      | 1-4        |
| Cast Spell        | Left Mouse |
| Aim Spell         | Aim Mouse  |
| Enter chat        | Enter      |


### Features we wanted but couldn't implement in time
 - Party system
 - Reflective surfaces
 - Shields
 - Sound effects
 - Better map generation

##### A special thanks to **dragondeplatino** and **DawnBringer** for designing the [DawnLike Tileset](http://opengameart.org/content/dawnlike-16x16-universal-rogue-like-tileset-v181) without which Netrogue wouldn't exist. We upscaled the textures using hq4x.
