package sirius.rendering.fonts;

import org.lwjgl.BufferUtils;
import sirius.encode_tools.Encode;
import sirius.utils.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class Font {
    private transient java.awt.Font font;
    private transient int textureId;
    private final transient String FILEPATH;
    private transient int size;

    // private int fontImageHeight, lineHeight;
    private Map<Integer, Glyph> glyphMap;

    private transient int advance;
    private transient FontMetrics fontMetrics;

    public Font(String FILEPATH, int fontSize) {
        this.FILEPATH = FILEPATH;
        this.size = fontSize;

        try {
            this.font = java.awt.Font.createFont(java.awt.Font.PLAIN, new File(FILEPATH));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        font = font.deriveFont(java.awt.Font.PLAIN, size);

        Font newFont = searchForPropertiesFiles(FILEPATH, fontSize);
        if (newFont == null) {
            this.size = fontSize;
            this.glyphMap = new HashMap<>();

            BufferedImage bitmap = generateBitmap();

            calculateGlyphsProperties(bitmap);

            uploadTexture(bitmap);

            // exportBitmap(bitmap);

            Encode.saveFont(this, Settings.Files.FONTS_PROPERTIES_FOLDER + "/" + font.getName() + fontSize + ".json");
        } else {
            this.glyphMap = new HashMap<>(newFont.glyphMap);
            this.size     = fontSize;

            BufferedImage bitmap = generateBitmap();
            uploadTexture(bitmap);
        }
    }

    /**
     * // TODO: 04/07/2022 Note this
     * // TODO: 20/07/2022 Try to not use this
     * Created due to Java abstraction.
     *
     * @param newFont
     */
    public Font(Font newFont) {
        this.FILEPATH = newFont.FILEPATH;
        this.size      = newFont.size;
        this.glyphMap  = new HashMap<>(newFont.glyphMap);
        this.textureId = newFont.textureId;
        this.advance   = newFont.advance;

        try {
            this.font = java.awt.Font.createFont(java.awt.Font.PLAIN, new File(FILEPATH));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        font = font.deriveFont(java.awt.Font.PLAIN, size);

        BufferedImage bitmap = generateBitmap();

        calculateGlyphsProperties(bitmap);

        uploadTexture(bitmap);

        // exportBitmap(bitmap);
    }

    public Glyph getCharacter(int codepoint) {
        return glyphMap.getOrDefault(codepoint, new Glyph(0, 0, 0, 0));
    }

    public BufferedImage generateBitmap() {
        // Create fake image to get font information
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        this.fontMetrics = g2d.getFontMetrics();

        calculateAdvance(font);

        final int LINE_HEIGHT = fontMetrics.getHeight();
        final int LINE_WIDTH  = this.advance * 41;  // 41 chars per line  in the bitmap. If you change the '41' value,
                                                    // change for an odd number, 1, 3, 5, 7, 9...
                                                    // To maintain the integrity of xBearing and yBearing calculations

        int totalFontImgHeight = fontMetrics.getHeight();

        // System.out.println("Name: " + font.getName());
        // System.out.println("W: " + this.advance);
        // System.out.println("H:" + LINE_HEIGHT);

        int xCurrent = 0;
        int yCurrent = LINE_HEIGHT;

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            // If we can display this codepoint (ascii table)
            if (font.canDisplay(i)) {
                // Get the sizes for each codepoints glyph, and update the actual width and height
                // Glyph glyph = new Glyph(x + breaker, y, charWidth, lineHeight, advance, bearingX, bearingY);
                Glyph glyph = new Glyph(xCurrent, yCurrent, fontMetrics.charWidth(i), LINE_HEIGHT);
                glyphMap.put(i, glyph);

                xCurrent += this.advance;

                // End of the line
                if (xCurrent >= LINE_WIDTH) {
                    xCurrent = 0;
                    yCurrent += LINE_HEIGHT;
                    totalFontImgHeight += LINE_HEIGHT;
                }
            }
        }

        totalFontImgHeight += fontMetrics.getHeight();
        g2d.dispose();

        img = new BufferedImage(LINE_WIDTH, totalFontImgHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        int currentDisplayableGlyph = 0;
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                currentDisplayableGlyph++;
                Glyph glyph = glyphMap.get(i);
                // TODO: 14/07/2022 Not working --I think that this is working...
                if (currentDisplayableGlyph % 2 == 0) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(Color.RED);
                }
                /* Sometimes Graphics2D fails loading some glyphs.
                 * Full details in https://support.oracle.com/knowledge/Middleware/2544450_1.html#FIX
                 */
                g2d.drawString("" + (char) i, glyph.x, glyph.y);
            }
        }

        g2d.setColor(Color.BLACK);
        g2d.dispose();

        return img;
    }

    // TODO: 16/07/2022 Explain
    private void calculateAdvance(java.awt.Font font) {
        // Note how big is the biggest character and save that width to after use it to make the bitmap grid
        int _advance = 0;
        // int hitter = 0;

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                if (fontMetrics.charWidth(i) > _advance) {
                    _advance = fontMetrics.charWidth(i);
                    // hitter = (int) (font.getSize() / 8.0f);
                }
            }
        }

        this.advance = _advance;
    }

    private void calculateGlyphsProperties(BufferedImage bitmap) {
        int xCurrent = 0;
        int yCurrent = 0;

        final int LINE_WIDTH = bitmap.getWidth();
        final int LINE_HEIGHT = this.fontMetrics.getHeight();

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (this.font.canDisplay(i)) {
                int yBearing = calculateYBearing(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT);

                int charWidth = this.fontMetrics.charWidth(i);
                int charHeight = calculateHeight(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT, yBearing);

                final int DELTA = charHeight - yBearing;
                int xBearing = calculateXBearing(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT, DELTA);

                int d = LINE_HEIGHT - yBearing;

                Glyph glyph = glyphMap.get(i);
                glyph.xBearing = xBearing;
                glyph.yBearing = yBearing;
                glyph.width = charWidth;
                glyph.height = charHeight;
                glyph.d = d;

                glyph.calculateTextureCoordinates(bitmap.getWidth(), bitmap.getHeight());
                xCurrent += advance;

                // End of the line
                if (xCurrent >= LINE_WIDTH) {
                    xCurrent = 0;
                    yCurrent += LINE_HEIGHT;
                }
            }
        }
    }

    private void uploadTexture(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * Integer.BYTES);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponent = (byte) ((pixel >> 24) & 0xFF);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
            }
        }
        buffer.flip();

        textureId = glGenTextures();

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        buffer.clear();
    }

    // TODO: 12/07/2022 document and explain weaknesses
    // TODO: 13/07/2022 Calculate new xBearing based in yBearing
    private int calculateXBearing(BufferedImage img, int xBegin, int yBegin, int advance, int lineHeight, int delta) {
        int xBearing = Integer.MAX_VALUE;

        final int X_FINAL = xBegin + advance;

        // todo Problem when height > yBearing
        final int Y_FINAL = yBegin + lineHeight + delta;

        int pixel = 0x000000;

        // When find the first white pixel, we will know how much is xBearing
        for (int xx = xBegin; xx < X_FINAL; xx++) {
            for (int yy = yBegin; yy < Y_FINAL; yy++) {
                pixel = img.getRGB(xx, yy);

                if (pixel != 0x000000) {
                    xBearing = xx - xBegin;
                    break;
                }
            }

            if (pixel != 0x000000) break;
        }

        return xBearing;
    }

    private int calculateYBearing(BufferedImage img, int xBegin, int yBegin, int advance, int lineHeight) {
        // TODO: 13/07/2022 Improve and think of a better solution
        int yBearing = 0;

        final int X_FINAL = xBegin + advance;
        final int Y_FINAL = yBegin + lineHeight;

        int targetPixel = 0;
        boolean targetPixelLocated = false;

        for (int yy = yBegin + lineHeight; yy > yBegin; yy--) {
            for (int xx = xBegin; xx < X_FINAL; xx++) {
                int pixel = img.getRGB(xx, yy);

                if (isBlue(pixel) || isRed(pixel)) {
                    targetPixel = pixel;
                    targetPixelLocated = true;
                }

                if (targetPixelLocated)
                    break;
            }

            if (targetPixelLocated)
                break;
        }

        boolean mayBreak = false;
        int pixel;
        for (int yy = yBegin; yy < Y_FINAL; yy++) {
            for (int xx = xBegin; xx < X_FINAL; xx++) {
                pixel = img.getRGB(xx, yy);

                if (isBlue(targetPixel) && isBlue(pixel)) {
                    yBearing = Y_FINAL - yy;
                    mayBreak = true;
                    break;
                }

                if (isRed(targetPixel) && isRed(pixel)) {
                    yBearing = Y_FINAL - yy;
                    mayBreak = true;
                    break;
                }
            }

            if (mayBreak) break;
        }

        return yBearing;
    }

    private int calculateHeight(BufferedImage img, int xPos, int yPos, int advance, int lineHeight, int yBearing) {
        int height = yBearing;

        final int X_BEGIN = xPos;
        final int Y_BEGIN = yPos + lineHeight;

        final int X_FINAL = xPos + advance;
        final int Y_FINAL = Y_BEGIN + lineHeight;

        int pixel = 0;
        int pixelUnder = 0;

        for (int yy = Y_BEGIN; yy < Y_FINAL; yy++) {
            boolean hasWhitePixelInXx = false;

            for (int xx = X_BEGIN; xx < X_FINAL; xx++) {
                pixel = img.getRGB(xx, yy);

                if (yy + 1 < img.getHeight())
                    pixelUnder = img.getRGB(xx, yy + 1);

                if (pixel != 0x000000) {
                    hasWhitePixelInXx = true;

                    if (pixelUnder == 0x000000) {
                        height = yBearing + (yy - Y_BEGIN + 1);
                    }
                }
            }

            if (!hasWhitePixelInXx)
                break;
        }

        return height;
    }

    private boolean isBlue(int pixel) {
        Color color = new Color(pixel);
        return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() != 0;
    }

    private boolean isRed(int pixel) {
        Color color = new Color(pixel);
        return color.getRed() != 0 && color.getGreen() == 0 && color.getBlue() == 0;
    }

    private void exportBitmap(BufferedImage bitmap) {
        try {
            File file = new File("assets/fonts/cache/" + font.getName() + ".png");
            ImageIO.write(bitmap, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Font searchForPropertiesFiles(String filepath, int fontSize) {
        File propertiesFolder = new File(Settings.Files.FONTS_PROPERTIES_FOLDER);

        List<String> propertiesFilesNamesList = new ArrayList<>();
        List<String> propertiesFilesPathsList = new ArrayList<>();

        // Get the names and the paths of the properties files
        for (int i = 0; i < Objects.requireNonNull(propertiesFolder.list()).length; i++ ) {
            String probablyPropertyFont = Objects.requireNonNull(propertiesFolder.list())[0];
            if (probablyPropertyFont.endsWith(".json")) {
                propertiesFilesNamesList.add(probablyPropertyFont.split(".json")[0]);
                propertiesFilesPathsList.add(Objects.requireNonNull(propertiesFolder.listFiles())[i].getPath());
            }
        }

        // Ex.: From 'assets\fonts\folks\Folks-Light.ttf' get Folks-Light
        String fontName = filepath.split("(/)|(\\\\)")[filepath.split("(/)|(\\\\)").length - 1].split(".ttf")[0];

        // Check if glyphs' properties from desired font are saved in cache directory
        for (int i = 0; i < propertiesFilesNamesList.size(); i++) {
            String propertyFile = propertiesFilesNamesList.get(i);
            // If the property file has the same name of the font, we are going to choose that property's file
            if (propertyFile.equals(fontName + fontSize)) {
                return Encode.getFontProperty(propertiesFilesPathsList.get(i));
            }
        }

        return null;
    }

    public int getGreatestHeight() {
        int greatestHeight = 0;

        for (int i = 0; i < glyphMap.size(); i++) {
            Glyph glyph = glyphMap.get(i);
            if (glyph == null) {
                continue;
            }

            if (greatestHeight < glyph.height)
                greatestHeight = glyph.height;
        }

        return greatestHeight;
    }

    public int getSize() {
        return this.size;
    }

    public int getTextureId() {
        return textureId;
    }
}
