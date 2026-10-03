# Versions Changelog

* **x.Y versions** are either updates that add content or major bug fixes
* **x.y.Z versions** are either small content update (language translation, keybind change, ...) or bug fixes

## v1.1.4
* Fix music fading out and restarting in a loop in creative mode and in the End
* Fix TIMM's End songs being cut right after they start
* Fix structure music restarting over and over when two structures are nearby
* Fix music never moving on to the next song while in a pale garden
* Fix the "Now playing" toast and `/nowplaying` sometimes losing the song name
* Fix the "reset delay on biome switch" option using seconds as ticks
* Fix TIMM's menu songs never playing in the main menu
* Fix the mushroom fields songs (and a few modded biome songs) never playing because of wrong playlist ids
* Fix the ice spikes songs never playing: no playlist used them
* Fix structure music playing only once per structure type, until another kind of structure was visited
* Fix the music staying quieter or jumping in volume after a fade was interrupted (song ending, world change)
* Fix the song of a structure from the previous world or dimension playing after travelling
* Add playlists for the new dappled forest and sulfur caves biomes
* Fix the biomes of Oh The Biomes We've Gone never getting TIMM's songs: the mod changed its id from `byg` to
  `biomeswevegone`
* Fix misspelled biome ids of Biomes O' Plenty, Regions Unexplored and Wythers, whose songs never played
* Add playlists for the newer biomes of Terralith, Nature's Spirit, Biomes O' Plenty, Regions Unexplored, BetterNether
  and Wythers
* Stream the music instead of loading every song played into memory, where each one (up to ~55 MB) stayed until
  resources were reloaded
* Spread the structure checks of the players over the second, check each nearby structure only once instead of once
  per chunk, and skip players without the mod

## v1.1.3
* Add Music Notification support for all 147 songs: title, author, album and TIMM cover are shown in the notifications,
  and every song can be played on its own from the Music Notification jukebox screen

# v1.1.2
* Add an option to disable music fading on biome switch
* Add a safeguard for null fade duration to prevent bugs

## v1.1.1
* Fix menu playing only Timm songs (no vanilla ones)
* Fix end music playing only vanilla songs (no Timm ones!)

## v1.1
* Add music fade out on biome switch
  * Do not fade out if the current biome song is available in the new biome
  * Starts fading after a configurable delay to avoid too many undesired biome switches
  * Duration of fading is configurable
  * After the fade out, a new song is played instantly. An option is available to reset the delay between song after it
* Add 21 structure songs
  * Structure songs play when a player gets close to a structure (distance depends on the structure), fading out current
  playing song if any
  * This feature requires the mod to be on the server too to detect the structures
  * This feature can be disabled in the settings
  * By default, a structure song plays till the end (unless entering another structure). There is an option to let it
  fade out if switching biomes
* Music is now properly detected by the **Music Control** mod (more compatibility will come in the future)
* Music has been resampled to make the mod lighter
* Vanilla music has been readded to be able to play along with TIMM's songs
* Vanilla music toasts display timm song correctly

## v1.0.6
* Entire mod rewrite for better maintainability, lots of possible bug fixes
* Remove unused songs
* Add 12 new biome songs
* Add `timmhelp`, a help command printing all available commands added by the mod, along with
  a short description
* Add `next` as an alias for the `skip` command
* Remove the possibility to skip to a specific sound event as it was barely usable without knowing actual
  sound events added by the mod
* Add a display name to songs for a better display when using the `nowplaying` command
* Remove the configuration of the delay between songs in the menu, to be always just a few seconds
* Add an option to display or not song info on skip
* Add French translation
* Use *Cloth Config* mod to handle the configuration of the mod. This is a new dependency
* Add Music Notification mod support
