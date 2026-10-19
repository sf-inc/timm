# Versions Changelog

* **x.Y versions** are either updates that add content or major bug fixes
* **x.y.Z versions** are either small content update (language translation, keybind change, ...) or bug fixes

## v1.1.3
* Fix the menu playlist never being used: the menu only played vanilla music
* Fix vanilla music (End, menu, modded biomes music) cutting Timm songs short
* Fix reset delay on biome switch delay value: the value was reset with the time in seconds instead of ticks
* Fix mushroom biomes (Mushroom Fields, Terralith's Mirage Isles, Wythers' Mushroom Caves and Mushroom Island) and
  BetterNether's Nether Grasslands never playing their Timm songs
* Fix Ice Spikes playing Snowy Plains songs instead of its own
* Spread structure detection between players over time as intended, instead of checking every player on the same tick
* Fix a structure song restarting right after it ends when re-entering its structure while it is still playing
* Fix Creative, End, underwater and dragon songs fading out on biome switch
* Fix two nearby structures switching back and forth between their songs
* Fix a structure song never playing again when visiting another structure of the same kind
* Update modded biome playlists: add missing biomes, fix misspelled biome ids, replace Oh The Biomes You'll Go
  with Oh The Biomes We've Gone, and let modded biomes also play their default music

## v1.1.2
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
