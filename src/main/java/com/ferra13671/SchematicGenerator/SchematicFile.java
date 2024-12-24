package com.ferra13671.SchematicGenerator;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SchematicFile {
    public boolean compiled = false;
    public boolean startedCompile = false;
    public final File file;

    public SchematicFile(File file) {
        this.file = file;
    }

    public void compile() {
        startedCompile = true;
        Thread thread = new Thread(() -> {
            Schematic schematic;
            CompoundTag compound;
            String schFile = file.getName().replace(SchematicGenerator.getFileExtension(file.getName()), "nbt");
            String schName = schFile.replace(".nbt", "");

            try {
                schematic = Schematic.fromBufferedImage(ImageIO.read(file));
                compound = schematic.compile();
            } catch (Exception e) {
                SchematicGenerator.error(e, String.format("An error occurred while reading and creating the schematic '%s' of the file '%s'", schName, file.getAbsolutePath()));
                return;
            }
            Path resultPath = Paths.get(SchematicGenerator.settings.outFolder + "/" + schFile);

            try {
                if (Files.exists(resultPath)) Files.delete(resultPath);
                Files.createFile(resultPath);
                NBTUtil.write(compound, resultPath.toFile());
                SchematicGenerator.log(String.format("Schematic '%s' has been successfully saved to file '%s'.", schName, resultPath.toFile().getAbsolutePath()));
            } catch (IOException e) {
                SchematicGenerator.error(e, String.format("An error occurred when saving the schematic '%s' to a file '%s'.", schName, resultPath.toFile().getAbsolutePath()));
            }
            compiled = true;
            SchematicGenerator.compiledSchematics++;
        });
        thread.start();
    }
}
