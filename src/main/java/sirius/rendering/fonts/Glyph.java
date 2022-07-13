package sirius.rendering.fonts;

import org.joml.Vector2f;

public class Glyph {
    public int x, y;
    public int width, height;

    public int xBearing;
    public int yBearing;

    public int d;

    public Vector2f[] textureCoordinates = new Vector2f[2];

    public Glyph(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void calculateTextureCoordinates(int imgFontWidth, int imgFontHeight) {
        float xMin = (float) (x + xBearing) / (float) imgFontWidth;
        float xMax = (float) (x + width + xBearing) / (float) imgFontWidth;

        float yMin = (float) (y - yBearing) / (float) imgFontHeight;
        float yMax = (float) (y + (height - yBearing)) / (float) imgFontHeight;


        textureCoordinates[0] = new Vector2f(xMin, yMax);
        textureCoordinates[1] = new Vector2f(xMax, yMin);
    }
}
