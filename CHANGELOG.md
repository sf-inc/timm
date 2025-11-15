# Versions Changelog

* **x.Y versions** are either updates that add content or major bug fixes
* **x.y.Z versions** are either small content update (language translation, keybind change, ...) or bug fixes

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
