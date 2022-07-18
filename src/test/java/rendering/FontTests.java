package rendering;

import org.junit.jupiter.api.Test;
import sirius.rendering.fonts.Font;
import sirius.rendering.fonts.Glyph;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FontTests {
    @Test
    public void aGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph a = font.getCharacter('a');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'a' char...");
        System.out.println("xBearing: " + a.xBearing);
        System.out.println("yBearing: " + a.yBearing);
        System.out.println("Height: " + a.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 1);
        System.out.println("yBearing: " + 17);
        System.out.println("Height: " + 17);
        assertTrue(a.xBearing == 1 && a.yBearing == 17 && a.height == 17);
    }

    @Test
    public void dGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph d = font.getCharacter('d');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'd' char...");
        System.out.println("xBearing: " + d.xBearing);
        System.out.println("yBearing: " + d.yBearing);
        System.out.println("Height: " + d.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 1);
        System.out.println("yBearing: " + 23);
        System.out.println("Height: " + 23);
        assertTrue(d.xBearing == 1 && d.yBearing == 23 && d.height == 23);
    }

    @Test
    public void iGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph i = font.getCharacter('i');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'i' char...");
        System.out.println("xBearing: " + i.xBearing);
        System.out.println("yBearing: " + i.yBearing);
        System.out.println("Height: " + i.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 2);
        System.out.println("yBearing: " + 23);
        System.out.println("Height: " + 23);
        assertTrue(i.xBearing == 2 && i.yBearing == 23 && i.height == 23);
    }

    @Test
    public void jGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph j = font.getCharacter('j');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'j' char...");
        System.out.println("xBearing: " + j.xBearing);
        System.out.println("yBearing: " + j.yBearing);
        System.out.println("Height: " + j.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 2);
        System.out.println("yBearing: " + 23);
        System.out.println("Height: " + 29);
        assertTrue(j.xBearing == 6 && j.yBearing == 23 && j.height == 29);
    }

    @Test
    public void cedilhaGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph cedilha = font.getCharacter('Ç');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'Ç' char...");
        System.out.println("xBearing: " + cedilha.xBearing);
        System.out.println("yBearing: " + cedilha.yBearing);
        System.out.println("Height: " + cedilha.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 1);
        System.out.println("yBearing: " + 23);
        System.out.println("Height: " + 30);
        assertTrue(cedilha.xBearing == 1 && cedilha.yBearing == 23 && cedilha.height == 30);
    }

    @Test
    public void arabicStartOfRubElHizbGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph arabic = font.getCharacter('۞');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing '۞' char...");
        System.out.println("xBearing: " + arabic.xBearing);
        System.out.println("yBearing: " + arabic.yBearing);
        System.out.println("Height: " + arabic.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 1);
        System.out.println("yBearing: " + 27);
        System.out.println("Height: " + 30);
        assertTrue(arabic.xBearing == 1 && arabic.yBearing == 27 && arabic.height == 30);
    }

    @Test
    public void arabicLetterSheenGlyphBoundaries() {
        Font font = new Font("assets/fonts/verdana.ttf", 32, true);
        Glyph arabic = font.getCharacter('ش');

        System.out.println("- - - - - - - - -");
        System.out.println("Testing 'ش' (arabic letter sheen) char...");
        System.out.println("xBearing: " + arabic.xBearing);
        System.out.println("yBearing: " + arabic.yBearing);
        System.out.println("Height: " + arabic.height);
        System.out.println("- - Expected - -");
        System.out.println("xBearing: " + 1);
        System.out.println("yBearing: " + 24);
        System.out.println("Height: " + 26);
        assertTrue(arabic.xBearing == 1 && arabic.yBearing == 24 && arabic.height == 26);
    }
}
