package com.ferra13671.SchematicGenerator;

import com.google.gson.*;
import net.querz.nbt.tag.StringTag;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SetupManager {
    private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

    protected static Settings loadSettings() {
        try {
            Path settingPath = Paths.get("settings.json");
            if (!Files.exists(settingPath)) {
                createSettingFile();
                return loadSettingsFromStream(SetupManager.class.getClassLoader().getResourceAsStream("resources/defaultSettings.json"));
            }
            return loadSettingsFromStream(Files.newInputStream(settingPath));
        } catch (IOException e) {
            return null;
        }
    }

    protected static boolean createFiles() {
        try {
            Path inFolder = Paths.get(SchematicGenerator.settings.inFolder);
            Path outFolder = Paths.get(SchematicGenerator.settings.outFolder);
            if (!Files.exists(outFolder))
                Files.createDirectory(outFolder);
            if (!Files.exists(SchematicGenerator.settings.logsPath))
                Files.createDirectory(SchematicGenerator.settings.logsPath);
            createREADME("EN");
            createREADME("RU");
            if (!Files.exists(inFolder)) {
                Files.createDirectory(inFolder);
                return false;
            }
        } catch (IOException e) {
            SchematicGenerator.error(e, "An error occurred while creating the application file system.");
            return false;
        }
        return true;
    }

    private static void createREADME(String lang) throws IOException {
        Path path = Paths.get("README_" + lang + ".txt");
        if (!Files.exists(path)) {
            Files.createFile(path);

            List<String> readmeText = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(SetupManager.class.getClassLoader().getResourceAsStream("resources/README_" + lang + ".txt")));
            String line = reader.readLine();
            while (line != null) {
                readmeText.add(line);
                line = reader.readLine();
            }

            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            for (String text : readmeText) {
                writer.write(text + System.lineSeparator());
            }
            writer.close();
        }
    }

    private static Settings loadSettingsFromStream(InputStream stream) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        if (jsonObject.get("inFolder") == null ||
                jsonObject.get("outFolder") == null ||
                jsonObject.get("DataVersion") == null ||
                jsonObject.get("SizeScale") == null ||
                jsonObject.get("DisableSaveAirData") == null ||
                jsonObject.get("MaxThreads") == null
        ) {
            createSettingFile();
            return loadSettingsFromStream(SetupManager.class.getClassLoader().getResourceAsStream("resources/defaultSettings.json"));
        }

        Settings settings = new Settings(
                jsonObject.get("inFolder").getAsString(),
                jsonObject.get("outFolder").getAsString(),
                jsonObject.get("DataVersion").getAsInt(),
                jsonObject.get("SizeScale").getAsDouble(),
                jsonObject.get("DisableSaveAirData").getAsBoolean(),
                jsonObject.get("MaxThreads").getAsInt()
        );

        if (jsonObject.get("palettes") != null) {
            jsonObject.get("palettes").getAsJsonArray().asList().forEach(jsonElement -> {
                JsonObject object = jsonElement.getAsJsonObject();
                if (
                        object.get("BlockName") != null &&
                                object.get("Min Red") != null &&
                                object.get("Min Green") != null &&
                                object.get("Min Blue") != null &&
                                object.get("Max Red") != null &&
                                object.get("Max Green") != null &&
                                object.get("Max Blue") != null &&
                                object.get("Priority") != null
                ) {
                    settings.customPalettes.add(new Settings.Palette(object.get("BlockName").getAsString(),
                            new Color(object.get("Min Red").getAsInt(), object.get("Min Green").getAsInt(), object.get("Min Blue").getAsInt()),
                            new Color(object.get("Max Red").getAsInt(), object.get("Max Green").getAsInt(), object.get("Max Blue").getAsInt()),
                            new Settings.PaletteInfo(new SchematicGenerator.SimpleTag("Name", new StringTag(object.get("BlockName").getAsString())), settings.customPalettes.size() + 1, object.get("Priority").getAsInt())));
                }
            });
        }

        return settings;
    }

    private static void createSettingFile() throws IOException {
        Path path = Paths.get("settings.json");
        if (Files.exists(path)) Files.delete(path);
        Files.createFile(path);
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("inFolder", new JsonPrimitive("include"));
        jsonObject.add("outFolder", new JsonPrimitive("output"));
        jsonObject.add("DataVersion", new JsonPrimitive(3700));
        jsonObject.add("SizeScale", new JsonPrimitive(1.0));
        jsonObject.add("DisableSaveAirData", new JsonPrimitive(true));
        jsonObject.add("MaxThreads", new JsonPrimitive(4));
        JsonArray jsonArray = new JsonArray();
        JsonObject paletteObject = new JsonObject();;
        putPalette(paletteObject, "minecraft:obsidian", 0, 0, 0, 128, 128, 128);
        jsonArray.add(paletteObject);
        jsonObject.add("palettes", jsonArray);
        writer.write(gson.toJson(JsonParser.parseString(jsonObject.toString())));
        writer.close();
    }

    private static void putPalette(JsonObject paletteObject, String blockName, int minRed, int minGreen, int minBlue, int maxRed, int maxGreen, int maxBlue) {
        paletteObject.add("BlockName", new JsonPrimitive(blockName));
        paletteObject.add("Min Red", new JsonPrimitive(minRed));
        paletteObject.add("Min Green", new JsonPrimitive(minGreen));
        paletteObject.add("Min Blue", new JsonPrimitive(minBlue));
        paletteObject.add("Max Red", new JsonPrimitive(maxRed));
        paletteObject.add("Max Green", new JsonPrimitive(maxGreen));
        paletteObject.add("Max Blue", new JsonPrimitive(maxBlue));
        paletteObject.add("Priority", new JsonPrimitive(0));
    }
}
