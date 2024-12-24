package com.ferra13671.SchematicGenerator;

import net.querz.nbt.tag.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SchematicGenerator {
    public static Settings settings;
    public static PrintWriter logger;

    public static List<SchematicFile> schematicFiles = new CopyOnWriteArrayList<>();
    public static int compiledSchematics;

    public static void main(String[] args) {
        settings = SetupManager.loadSettings();
        if (!SetupManager.createFiles()) return;
        try {
            Path path = Paths.get(settings.logsPath + "/" + getLogName() + ".log");
            if (Files.exists(path)) Files.delete(path);
            Files.createFile(path);
            logger = new PrintWriter(path.toFile());
        } catch (IOException ignored) {}

        File folder = Paths.get(settings.inFolder).toFile();
        File[] files = folder.listFiles();

        if (files != null) {
            List<File> files2 = Arrays.stream(files).filter(file -> isImage(file.getName())).toList();

            if (!files2.isEmpty()) {
                log(String.format("Found %s files, starting schematics generation...", files2.size()));
                for (File file : files2) {
                    schematicFiles.add(new SchematicFile(file));
                }
                while (!schematicFiles.isEmpty()) {
                    int startedThreads = 0;
                    for (SchematicFile schematicFile : schematicFiles) {
                        if (!schematicFile.compiled) {
                            if (!schematicFile.startedCompile) schematicFile.compile();
                            startedThreads++;
                        } else schematicFiles.remove(schematicFile);
                        if (startedThreads >= settings.maxThreads) break;
                    }
                    Thread.yield();
                }
            } else log("No image files found, shutdown.");
        } else log("No image files found, shutdown.");

        logger.close();
    }

    public static String getFileExtension(String filePath) {
        int index = filePath.indexOf('.');
        return index == -1? null : filePath.substring(index).replace(".", "");
    }

    public static boolean isImage(String path) {
        String extension = getFileExtension(path);
        if (extension != null) {
            switch (extension) {
                case "jpg":
                case "png":


                case "ico"://
                case "gif"://      --- IDK if Java will handle these types of images properly.
                case "tiff"://         However, they are still images, so I added them here.
                case "tif"://
                case "webP"://
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static CompoundTag createCompound(SimpleTag... tags) {
        CompoundTag compoundTag = new CompoundTag();
        for (SimpleTag simpleTag : tags) {
            compoundTag.put(simpleTag.name(), simpleTag.tag());
        }
        return compoundTag;
    }

    protected static void log(String message) {
        logger.write(message + System.lineSeparator());
        System.out.println(message);
    }

    protected static void error(Exception e, String message) {
        log(message);
        e.printStackTrace(logger);

        System.err.println(message);
        e.printStackTrace();
    }

    private static String getLogName() {
        return "log-" + Calendar.getInstance().get(Calendar.MONTH) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "." + Calendar.getInstance().get(Calendar.MINUTE);
    }

    public record SimpleTag(String name, Tag<?> tag) {}
}
