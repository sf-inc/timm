package com.github.charlyb01.timm.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Timm Configuration"))
                .setSavingRunnable(Config.SPEC::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder
                .startIntField(Component.literal("Minimum Delay"), Config.MIN_DELAY.get())
                .setDefaultValue(300)
                .setMin(0)
                .setMax(600)
                .setSaveConsumer(Config.MIN_DELAY::set)
                .build());

        general.addEntry(entryBuilder
                .startIntField(Component.literal("Maximum Delay"), Config.MAX_DELAY.get())
                .setDefaultValue(300)
                .setMin(0)
                .setMax(600)
                .setSaveConsumer(Config.MAX_DELAY::set)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Component.literal("Print on Skip"), Config.PRINT_ON_SKIP.get())
                .setDefaultValue(true)
                .setSaveConsumer(Config.PRINT_ON_SKIP::set)
                .build());

        return builder.build();
    }
}
