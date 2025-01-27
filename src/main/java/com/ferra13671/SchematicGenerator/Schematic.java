package com.ferra13671.SchematicGenerator;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Schematic {
    public final BufferedImage bufferedImage;
    public final int width;
    public final int height;

    public final ListTag<CompoundTag> blocks = new ListTag<>(CompoundTag.class);

    private Schematic(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        width = (int) (bufferedImage.getWidth() * SchematicGenerator.settings.sizeScale);
        height = (int) (bufferedImage.getHeight() * SchematicGenerator.settings.sizeScale);

        createBlockList();
    }

    public CompoundTag compile() {
        CompoundTag compound = new CompoundTag();
        addBlocks(compound);
        compound.put("palette", getTagPalettes());
        addSize(compound);
        compound.put("DataVersion", new IntTag(SchematicGenerator.settings.dataVersion));

        return compound;
    }

    private void createBlockList() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = bufferedImage.getRGB((int) (x / SchematicGenerator.settings.sizeScale), (int) (y / SchematicGenerator.settings.sizeScale));
                int blockState = filterPalettes(pixelColor);

                if (SchematicGenerator.settings.disableSaveAirData)
                    if (blockState == 0) continue;
                ListTag<IntTag> blockPos = new ListTag<>(IntTag.class);
                blockPos.add(new IntTag(x));
                blockPos.add(new IntTag(0));
                blockPos.add(new IntTag(y));
                CompoundTag block = SchematicGenerator.createCompound(
                        new SchematicGenerator.SimpleTag("pos", blockPos),
                        new SchematicGenerator.SimpleTag("state", new IntTag(blockState))
                );
                blocks.add(block);
            }
        }
    }

    private int filterPalettes(int pixelColor) {
        int pixelRed = pixelColor >> 16 & 255;
        int pixelGreen = pixelColor >> 8 & 255;
        int pixelBlue = pixelColor & 255;
        int pixelAlpha = pixelColor >>> 24;

        int value = 0, priority = -1;
        for (Settings.Palette palette : SchematicGenerator.settings.customPalettes) {
            if (palette.paletteInfo().priority < priority) continue;
            if (palette.minColor().equals(palette.maxColor())) {
                if (palette.minColor().equals(new Color(pixelRed, pixelGreen, pixelBlue, pixelAlpha))) value = palette.paletteInfo().id;
            } else {
                Color minColor = palette.minColor();
                Color maxColor = palette.maxColor();

                if (minColor.getRed() > maxColor.getRed() && minColor.getGreen() > maxColor.getGreen() && minColor.getBlue() > maxColor.getBlue()) {
                    int tempRed = minColor.getRed();
                    int tempGreen = minColor.getGreen();
                    int tempBlue = minColor.getBlue();
                    minColor = new Color(maxColor.getRed(), maxColor.getGreen(), maxColor.getBlue());
                    maxColor = new Color(tempRed, tempGreen, tempBlue);
                }

                if (pixelAlpha < 220) {
                    pixelRed = 255;
                    pixelGreen = 255;
                    pixelBlue = 255;
                }

                if (hasInRange(pixelRed, minColor.getRed(), maxColor.getRed()) &&
                        hasInRange(pixelGreen, minColor.getGreen(), maxColor.getGreen()) &&
                        hasInRange(pixelBlue, minColor.getBlue(), maxColor.getBlue())
                ) {
                    value = palette.paletteInfo().id;
                    priority = palette.paletteInfo().priority;
                }
            }
        }
        return value;
    }

    private boolean hasInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    private ListTag<CompoundTag> getTagPalettes() {
        ListTag<CompoundTag> palettes = new ListTag<>(CompoundTag.class);
        palettes.add(SchematicGenerator.createCompound(new SchematicGenerator.SimpleTag("Name", new StringTag("minecraft:air"))));
        for (Settings.Palette palette : SchematicGenerator.settings.customPalettes) {
            palettes.add(SchematicGenerator.createCompound(palette.paletteInfo().tag));
        }

        return palettes;
    }

    private void addBlocks(CompoundTag compound) {
        compound.put("blocks", blocks);
    }

    private void addSize(CompoundTag compound) {
        ListTag<IntTag> sizeTag = new ListTag<>(IntTag.class);
        sizeTag.add(new IntTag(width));
        sizeTag.add(new IntTag(1));
        sizeTag.add(new IntTag(height));

        compound.put("size", sizeTag);
    }


    public static Schematic fromURL(URL url) throws IOException {
        return fromBufferedImage(ImageIO.read(url));
    }

    public static Schematic fromInputStream(InputStream stream) throws IOException {
        return fromBufferedImage(ImageIO.read(stream));
    }

    public static Schematic fromBufferedImage(BufferedImage bufferedImage) {
        return new Schematic(bufferedImage);
    }
}
