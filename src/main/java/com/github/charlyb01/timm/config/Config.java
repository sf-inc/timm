package com.github.charlyb01.timm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MIN_DELAY;
    public static final ForgeConfigSpec.IntValue MAX_DELAY;
    public static final ForgeConfigSpec.BooleanValue PRINT_ON_SKIP;
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
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
