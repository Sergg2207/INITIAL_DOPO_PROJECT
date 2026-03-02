/**
 * Cup representa una taza en la torre de apilamiento.
 * Cada taza tiene un numero (tamaño), color y puede o no tener tapa.
 * La representacion visual se realiza con un rectangulo.
 * 
 * @author Sergio Gonzalez 
 * @version 15feb26
 */
public class Cup {
    // constante de escala visual (debe coincidir con Tower.SCALE)
    private static final int SCALE = 20;

    // atributos
    private int number;
    private String color;
    private int yPosition;
    private Rectangle rectangle;
    private boolean hasLid;

    public Cup(int number, String color) {
        this.number = number;
        this.color = color;
        this.yPosition = 0;
        this.hasLid = false;
        this.rectangle = new Rectangle();
        rectangle.changeColor(color);
        rectangle.changeSize(number * SCALE, number * SCALE);
    }

    public void makeVisible() {
        rectangle.changeColor(color);
        rectangle.makeVisible();
    }

    public void makeInvisible() {
        rectangle.makeInvisible();
    }

    public void moveTo(int x, int y) {
        rectangle.moveTo(x, y);
        yPosition = y;
    }

    public int getNumber() { return number; }
    public String getColor() { return color; }
    public int getWidth() { return number * SCALE; }
    public int getHeight() { return number; }
    public boolean hasLid() { return hasLid; }
    public void setHasLid(boolean value) { this.hasLid = value; }
    public int getYPosition() { return yPosition; }
}