package com.github.charlyb01.timm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue MIN_DELAY;
    public static final ModConfigSpec.IntValue MAX_DELAY;
    public static final ModConfigSpec.BooleanValue PRINT_ON_SKIP;
    public static final ModConfigSpec.BooleanValue DEBUG_LOG;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("general");

        MIN_DELAY = builder
                .comment("Minimum delay (in ticks)")
                .defineInRange("minDelay", 300, 0, 600);

        MAX_DELAY = builder
                .comment("Maximum delay (in ticks)")
                .defineInRange("maxDelay", 300, 0, 600);

        PRINT_ON_SKIP = builder
                .comment("Print message when skipping")
                .define("printOnSkip", true);

        DEBUG_LOG = builder
                .comment("Enable debug logging (no GUI entry)")
                .define("debugLog", false);

        builder.pop();
        SPEC = builder.build();
    }
}