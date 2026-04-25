package com.github.charlyb01.timm.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "general")
public class GeneralConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(max = 600)
    public int minDelay = 120;
    @ConfigEntry.BoundedDiscrete(max = 600)
    public int maxDelay = 300;

    public boolean enableMusicFading  = true;
    @ConfigEntry.BoundedDiscrete(max = 15)
    public int fadeDelay = 3;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    public int fadeDuration = 5;

    public boolean resetDelayOnBiomeSwitch = false;

    public boolean printOnSkip = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableStructureMusic = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public StructureFadeOut structureFadeOut = StructureFadeOut.NEVER;

    @ConfigEntry.Gui.Excluded
    public boolean debugLog = false;
}
