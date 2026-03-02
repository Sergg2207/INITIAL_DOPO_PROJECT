/**
 * Lid representa una tapa en la torre de apilamiento.
 * Cada tapa esta asociada a una taza por su numero.
 * Las tapas siempre tienen 1 cm de altura y el mismo color que su taza.
 * 
 * @author Sergio Gonzalez
 * @version 15feb26
 */
public class Lid {
    private static final int LID_HEIGHT = 1;
    private static final int SCALE = 20;

    private int cupNumber;
    private String color;
    private int yPosition;
    private Rectangle rectangle;

    public Lid(int cupNumber, String color) {
        this.cupNumber = cupNumber;
        this.color = color;
        this.yPosition = 0;
        this.rectangle = new Rectangle();
        rectangle.changeColor(color);
        rectangle.changeSize(LID_HEIGHT * SCALE, cupNumber * SCALE);
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

    public int getCupNumber() { return cupNumber; }
    public int getHeight() { return LID_HEIGHT; }
    public int getWidth() { return cupNumber * SCALE; }
    public int getYPosition() { return yPosition; }
}