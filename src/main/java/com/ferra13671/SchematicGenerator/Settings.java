package com.ferra13671.SchematicGenerator;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    public final String inFolder;
    public final String outFolder;
    public final int dataVersion;
    public final double sizeScale;
    public final boolean disableSaveAirData;
    public final int maxThreads;

    public final List<Palette> customPalettes = new ArrayList<>();

    public final Path logsPath = Paths.get("Logs");

    public Settings(Object... settings) {
        inFolder = (String) settings[0];
        outFolder = (String) settings[1];
        dataVersion = (Integer) settings[2];
        sizeScale = (Double) settings[3];
        disableSaveAirData = (Boolean) settings[4];
        maxThreads = (Integer) settings[5];
    }

    public record Palette(String blockName, Color minColor, Color maxColor, PaletteInfo paletteInfo) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Palette palette)) return false;

            return blockName.equals(palette.blockName);
        }
    }

    public static class PaletteInfo {
        public final int priority;
        public int id;
        public SchematicGenerator.SimpleTag tag;

        public PaletteInfo(SchematicGenerator.SimpleTag tag, int id, int priority) {
            this.tag = tag;
            this.id = id;
            this.priority = priority;
        }
    }
}
