package sirius.rendering.fonts;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class Font {
    private int textureId;
    private String filepath;
    private int size;

    // private int fontImageHeight, lineHeight;
    private Map<Integer, Glyph> glyphMap;

    private int advance;
    private FontMetrics fontMetrics;

    public Font(String filepath, int fontSize) {
        this(filepath, fontSize, false);
    }

    public Font(String filepath, int fontSize, boolean useForTests) {
        this.filepath     = filepath;
        this.size         = fontSize;
        this.glyphMap = new HashMap<>();

        java.awt.Font font = new java.awt.Font(filepath, java.awt.Font.PLAIN, size);
        BufferedImage bitmap = generateBitmap(font);
        calculateGlyphsProperties(font, bitmap);

        if (!useForTests) {
            System.out.println("Image Width: " + bitmap.getWidth());
            System.out.println("Line Height: " + fontMetrics.getHeight());
            uploadTexture(bitmap);
        }

        exportBitmap(bitmap);
    }

    /**
     * // TODO: 04/07/2022 Note this
     * Created due to Java abstraction.
     *
     * @param newFont
     */
    public Font(Font newFont) {
        this.filepath     = newFont.filepath;
        this.size         = newFont.size;
        this.glyphMap = newFont.glyphMap;
        this.textureId    = newFont.textureId;

        java.awt.Font font = new java.awt.Font(filepath, java.awt.Font.PLAIN, size);
        BufferedImage bitmap = generateBitmap(font);
        calculateGlyphsProperties(font, bitmap);
        uploadTexture(bitmap);
        exportBitmap(bitmap);
    }

    public Glyph getCharacter(int codepoint) {
        return glyphMap.getOrDefault(codepoint, new Glyph(0, 0, 0, 0));
    }

    public BufferedImage generateBitmap(java.awt.Font font) {
        // Create fake image to get font information
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        this.fontMetrics = g2d.getFontMetrics();

        // Note how big is the biggest character and save that width to after use it to make the bitmap grid
        int advance = 0;
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                if (fontMetrics.charWidth(i) > advance)
                    advance = fontMetrics.charWidth(i);
            }
        }

        this.advance = advance;

        final int LINE_HEIGHT = fontMetrics.getHeight();
        final int LINE_WIDTH  = advance * 41; // 41 chars per line  in the bitmap. If you change the '41' value,
                                              // change for an odd number, 1, 3, 5, 7, 9...
                                              // To maintain the integrity of xBearing and yBearing calculations

        int totalFontImgHeight = fontMetrics.getHeight();

        // int breaker = 5;
        // advance += breaker;

        int xCurrent = 0;
        int yCurrent = LINE_HEIGHT;

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            // If we can display this codepoint (ascii table)
            if (font.canDisplay(i)) {
                // Get the sizes for each codepoints glyph, and update the actual width and height
                // Glyph glyph = new Glyph(x + breaker, y, charWidth, lineHeight, advance, bearingX, bearingY);
                Glyph glyph = new Glyph(xCurrent, yCurrent, fontMetrics.charWidth(i), LINE_HEIGHT);
                glyphMap.put(i, glyph);

                xCurrent += advance;

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
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                Glyph glyph = glyphMap.get(i);
                if (i % 2 == 0) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(Color.RED);
                }
                g2d.drawString("" + (char) i, glyph.x, glyph.y);
            }
        }

        g2d.dispose();

        return img;
    }

    private void calculateGlyphsProperties(java.awt.Font font, BufferedImage bitmap) {
        int xCurrent = 0;
        int yCurrent = 0;

        final int LINE_WIDTH = bitmap.getWidth();
        final int LINE_HEIGHT = this.fontMetrics.getHeight();

        for (int i = 0; i < glyphMap.size(); i++) {
            if (font.canDisplay(i)) {
                // TODO: 13/07/2022 Create Gson file to keep the xBearing, height and yBearing
                int xBearing = calculateXBearing(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT);
                int yBearing = calculateYBearing(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT);

                int charWidth = this.fontMetrics.charWidth(i);
                int charHeight = calculateHeight(bitmap, xCurrent, yCurrent, advance, LINE_HEIGHT, yBearing);

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
    private int calculateXBearing(BufferedImage img, int xBegin, int yBegin, int advance, int lineHeight) {
        int xBearing = Integer.MAX_VALUE;

        final int X_FINAL = xBegin + advance;
        final int Y_FINAL = yBegin + lineHeight;

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
            File file = new File("assets/fonts/cache/tmp.png");
            ImageIO.write(bitmap, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTextureId() {
        return textureId;
    }
}
