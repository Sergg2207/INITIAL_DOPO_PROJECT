import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JOptionPane;

/**
 * Tower representa la torre de apilamiento de tazas y tapas.
 * Es la clase principal del simulador: gestiona la creacion, adicion,
 * eliminacion y reorganizacion de elementos, ademas del control visual.
 * 
 * @author Sergio Gonzalez
 * @version 15Feb26
 */
public class Tower {
    // atributos
    private int width;                    // ancho de la torre (cm)
    private int maxHeight;                // altura maxima (cm)
    private ArrayList<Cup> cups;          // lista de tazas
    private ArrayList<Lid> lids;          // lista de tapas
    private Canvas canvas;                // lienzo para dibujar
    private boolean visible;              // estado de visibilidad
    private boolean lastOperationOk;      // indica si la ultima operacion fue exitosa

    // constantes de visualizacion
    private static final int BASE_X = 50;    // posicion X base en pixeles
    private static final int BASE_Y = 550;   // posicion Y base (parte inferior del canvas)
    private static final int SCALE  = 20;    // factor de escala (cm a pixeles)

    /**
     * Constructor para objetos de la clase Tower.
     * 
     * @param width     ancho de la torre en cm
     * @param maxHeight altura maxima de la torre en cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.cups = new ArrayList<Cup>();
        this.lids = new ArrayList<Lid>();
        this.visible = true;
        this.lastOperationOk = true;
        this.canvas = Canvas.getCanvas();
        drawTowerBase();
    }

    // ===================== MANEJO DE TAZAS =====================

    /**
     * Adiciona una taza nueva a la torre generando su color automaticamente.
     * Solo puede existir una taza por cada numero.
     * 
     * @param number numero/tamaño de la taza en cm
     */
    public void pushCup(int number) {
        if (number <= 0) {
            showError("El numero de la taza debe ser positivo.");
            lastOperationOk = false;
            return;
        }
        if (findCupByNumber(number) != null) {
            showError("Ya existe una taza con el numero " + number + ".");
            lastOperationOk = false;
            return;
        }
        Cup newCup = new Cup(number, generateRandomColor());
        lastOperationOk = addCup(newCup);
        if (!lastOperationOk) {
            showError("No se pudo adicionar la taza. Verifique el espacio disponible.");
        } else if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Remueve la taza del tope de la torre (ultima ingresada).
     */
    public void popCup() {
        if (cups.isEmpty()) {
            showError("No hay tazas para remover.");
            lastOperationOk = false;
            return;
        }
        // primero borrar todo del canvas
        if (visible) {
            erase();
        }
        // luego remover de la lista
        removeLastCup();
        lastOperationOk = true;
        // redibujar lo que queda
        if (visible) {
            draw();
        }
    }

    /**
     * Remueve una taza especifica de la torre buscandola por su numero.
     * 
     * @param number numero de la taza a remover
     */
    public void removeCup(int number) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).getNumber() == number) {
                if (visible) { erase(); }
                removeCupByIndex(i);
                lastOperationOk = true;
                if (visible) { draw(); }
                return;
            }
        }
        showError("No se encontro una taza con el numero " + number + ".");
        lastOperationOk = false;
    }

    // ===================== MANEJO DE TAPAS =====================

    /**
     * Adiciona una tapa a la torre. La tapa toma el color de su taza.
     * Solo puede existir una tapa por cada numero.
     * 
     * @param number numero de la taza a la que pertenece la tapa
     */
    public void pushLid(int number) {
        if (number <= 0) {
            showError("El numero de la tapa debe ser positivo.");
            lastOperationOk = false;
            return;
        }
        if (findLidByNumber(number) != null) {
            showError("Ya existe una tapa para la taza numero " + number + ".");
            lastOperationOk = false;
            return;
        }
        Cup cup = findCupByNumber(number);
        String color = (cup != null) ? cup.getColor() : generateRandomColor();
        Lid newLid = new Lid(number, color);
        lastOperationOk = addLid(newLid);
        if (!lastOperationOk) {
            showError("No se pudo adicionar la tapa. Verifique el espacio disponible.");
        } else if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Remueve la tapa del tope de la torre (ultima ingresada).
     */
    public void popLid() {
        if (lids.isEmpty()) {
            showError("No hay tapas para remover.");
            lastOperationOk = false;
            return;
        }
        if (visible) { erase(); }
        removeLastLid();
        lastOperationOk = true;
        if (visible) { draw(); }
    }

    /**
     * Remueve una tapa especifica buscandola por el numero de su taza.
     * 
     * @param number numero de la taza cuya tapa se quiere remover
     */
    public void removeLid(int number) {
        for (int i = 0; i < lids.size(); i++) {
            if (lids.get(i).getCupNumber() == number) {
                if (visible) { erase(); }
                removeLidByIndex(i);
                lastOperationOk = true;
                if (visible) { draw(); }
                return;
            }
        }
        showError("No se encontro una tapa para la taza numero " + number + ".");
        lastOperationOk = false;
    }

    // ===================== REORGANIZACION =====================

    /**
     * Ordena los elementos de la torre de mayor a menor (base a cima).
     * El numero menor queda en la cima. Si la taza y su tapa estan en
     * la torre, la tapa se coloca sobre la taza. Solo incluye los que quepan.
     */
    public void orderTower() {
        Collections.sort(cups, new Comparator<Cup>() {
            public int compare(Cup c1, Cup c2) {
                return c2.getNumber() - c1.getNumber(); // descendente: mayor abajo
            }
        });
        reorderLidsAfterCups();
        removeElementsThatDontFit();
        lastOperationOk = true;
        if (visible) {
            erase();
            draw();
        }
    }

    /**
     * Invierte el orden actual de los elementos en la torre.
     * Solo incluye los elementos que quepan.
     */
    public void reverseTower() {
        Collections.reverse(cups);
        reorderLidsAfterCups();
        removeElementsThatDontFit();
        lastOperationOk = true;
        if (visible) {
            erase();
            draw();
        }
    }

    // ===================== CONSULTAS =====================

    /**
     * Calcula la altura total de los elementos apilados en la torre.
     * 
     * @return altura total en cm
     */
    public int height() {
        return getTotalHeight();
    }

    /**
     * Retorna los numeros de las tazas tapadas por sus tapas,
     * ordenados de menor a mayor.
     * 
     * @return array con numeros de las tazas que tienen tapa
     */
    public int[] lidedCups() {
        return getLidedCups();
    }

    /**
     * Retorna la informacion de los elementos apilados ordenados de base a cima.
     * Formato: {{"cup","4"},{"lid","4"}} en minusculas.
     * 
     * @return array 2D con [tipo, numero] de cada elemento
     */
    public String[][] stackingItems() {
        return getStackingInfo();
    }

    // ===================== VISIBILIDAD =====================

    /**
     * Hace visible el simulador y dibuja la torre.
     */
    public void makeVisible() {
        visible = true;
        draw();
    }

    /**
     * Hace invisible el simulador y borra la torre del canvas.
     */
    public void makeInvisible() {
        visible = false;
        erase();
    }

    // ===================== OTRAS OPERACIONES =====================

    /**
     * Termina el simulador.
     */
    public void exit() {
        erase();
        System.exit(0);
    }

    /**
     * Indica si la ultima operacion realizada fue exitosa.
     * 
     * @return true si la ultima operacion fue exitosa, false si no
     */
    public boolean ok() {
        return lastOperationOk;
    }

    // ===================== METODOS INTERNOS DE COLECCION =====================

    /**
     * Agrega una taza a la lista si cabe en la torre.
     * 
     * @param cup taza a agregar
     * @return true si se agrego, false si no cabe
     */
    public boolean addCup(Cup cup) {
        if (cup.getNumber() > width) {
            lastOperationOk = false;
            return false;
        }
        if (getTotalHeight() + cup.getHeight() > maxHeight) {
            lastOperationOk = false;
            return false;
        }
        cups.add(cup);
        lastOperationOk = true;
        return true;
    }

    /**
     * Agrega una tapa a la lista si cabe en la torre.
     * 
     * @param lid tapa a agregar
     * @return true si se agrego, false si no cabe
     */
    public boolean addLid(Lid lid) {
        if (getTotalHeight() + lid.getHeight() > maxHeight) {
            lastOperationOk = false;
            return false;
        }
        Cup cup = findCupByNumber(lid.getCupNumber());
        if (cup != null) {
            cup.setHasLid(true);
        }
        lids.add(lid);
        lastOperationOk = true;
        return true;
    }

    /**
     * Remueve una taza por su indice interno.
     * Si tenia tapa, la remueve tambien.
     * 
     * @param index indice de la taza en la lista
     * @return taza removida, o null si el indice es invalido
     */
    public Cup removeCupByIndex(int index) {
        if (index < 0 || index >= cups.size()) {
            return null;
        }
        Cup removed = cups.remove(index);
        if (removed.hasLid()) {
            removeLidByNumber(removed.getNumber());
            removed.setHasLid(false);
        }
        return removed;
    }

    /**
     * Remueve la ultima taza de la torre.
     * 
     * @return taza removida, o null si no hay tazas
     */
    public Cup removeLastCup() {
        if (cups.isEmpty()) {
            return null;
        }
        return removeCupByIndex(cups.size() - 1);
    }

    /**
     * Remueve una tapa por su indice interno.
     * Actualiza el estado hasLid de la taza correspondiente.
     * 
     * @param index indice de la tapa en la lista
     * @return tapa removida, o null si el indice es invalido
     */
    public Lid removeLidByIndex(int index) {
        if (index < 0 || index >= lids.size()) {
            return null;
        }
        Lid removed = lids.remove(index);
        Cup cup = findCupByNumber(removed.getCupNumber());
        if (cup != null) {
            cup.setHasLid(false);
        }
        return removed;
    }

    /**
     * Remueve la ultima tapa de la torre.
     * 
     * @return tapa removida, o null si no hay tapas
     */
    public Lid removeLastLid() {
        if (lids.isEmpty()) {
            return null;
        }
        return removeLidByIndex(lids.size() - 1);
    }

    /**
     * Remueve una tapa buscandola por numero de taza (uso interno).
     * 
     * @param cupNumber numero de la taza
     * @return tapa removida, o null si no existe
     */
    private Lid removeLidByNumber(int cupNumber) {
        for (int i = 0; i < lids.size(); i++) {
            if (lids.get(i).getCupNumber() == cupNumber) {
                return removeLidByIndex(i);
            }
        }
        return null;
    }

    /**
     * Calcula la altura total de todos los elementos apilados.
     * 
     * @return altura total en cm
     */
    public int getTotalHeight() {
        int total = 0;
        for (Cup cup : cups) {
            total += cup.getHeight();
            if (cup.hasLid()) {
                total += 1; // tapa siempre mide 1 cm
            }
        }
        return total;
    }

    /**
     * Retorna los numeros de las tazas que tienen tapa, ordenados de menor a mayor.
     * 
     * @return array con los numeros de las tazas tapadas
     */
    public int[] getLidedCups() {
        ArrayList<Integer> lidedNumbers = new ArrayList<Integer>();
        for (Cup cup : cups) {
            if (cup.hasLid()) {
                lidedNumbers.add(cup.getNumber());
            }
        }
        Collections.sort(lidedNumbers);
        int[] result = new int[lidedNumbers.size()];
        for (int i = 0; i < lidedNumbers.size(); i++) {
            result[i] = lidedNumbers.get(i);
        }
        return result;
    }

    /**
     * Retorna la informacion de todos los elementos apilados de base a cima.
     * 
     * @return array 2D con [tipo, numero] por elemento
     */
    public String[][] getStackingInfo() {
        ArrayList<String[]> info = new ArrayList<String[]>();
        for (Cup cup : cups) {
            info.add(new String[]{"cup", String.valueOf(cup.getNumber())});
            if (cup.hasLid()) {
                info.add(new String[]{"lid", String.valueOf(cup.getNumber())});
            }
        }
        String[][] result = new String[info.size()][2];
        for (int i = 0; i < info.size(); i++) {
            result[i] = info.get(i);
        }
        return result;
    }

    /**
     * Obtiene el numero de tazas actuales en la torre.
     * 
     * @return cantidad de tazas
     */
    public int getCupsCount() {
        return cups.size();
    }

    /**
     * Obtiene el numero de tapas actuales en la torre.
     * 
     * @return cantidad de tapas
     */
    public int getLidsCount() {
        return lids.size();
    }

    // ===================== VISUALIZACION =====================

    /**
     * Dibuja la torre completa con todos sus elementos desde la base hacia arriba.
     * Los elementos se centran respecto al ancho de la torre.
     */
    public void draw() {
        if (!visible) return;
        int towerCenterX = BASE_X + (width * SCALE / 2);
        int currentY = BASE_Y;
        for (Cup cup : cups) {
            int cupHeightPx = cup.getHeight() * SCALE;
            int cupWidthPx  = cup.getWidth();
            int cupX = towerCenterX - (cupWidthPx / 2);
            currentY -= cupHeightPx;
            cup.makeInvisible();
            cup.moveTo(cupX, currentY);
            cup.makeVisible();
            if (cup.hasLid()) {
                Lid lid = findLidByNumber(cup.getNumber());
                if (lid != null) {
                    int lidHeightPx = lid.getHeight() * SCALE;
                    int lidWidthPx  = lid.getWidth();
                    int lidX = towerCenterX - (lidWidthPx / 2);
                    currentY -= lidHeightPx;
                    lid.makeInvisible();
                    lid.moveTo(lidX, currentY);
                    lid.makeVisible();
                }
            }
        }
    }

    /**
     * Borra la representacion visual de todos los elementos de la torre.
     */
    public void erase() {
        for (Cup cup : cups) {
            cup.makeInvisible();
        }
        for (Lid lid : lids) {
            lid.makeInvisible();
        }
    }

    /**
     * Dibuja la base visual de la torre con marcas de centimetros de altura.
     * No se adicionan numeros, solo marcas visuales.
     */
    private void drawTowerBase() {
        canvas.setVisible(true);
        // marcas de altura se pueden implementar con Rectangle delgados
    }

    // ===================== METODOS AUXILIARES PRIVADOS =====================

    /**
     * Busca una taza por su numero.
     * 
     * @param number numero de la taza
     * @return la taza encontrada, o null si no existe
     */
    private Cup findCupByNumber(int number) {
        for (Cup cup : cups) {
            if (cup.getNumber() == number) {
                return cup;
            }
        }
        return null;
    }

    /**
     * Busca una tapa por el numero de su taza.
     * 
     * @param cupNumber numero de la taza
     * @return la tapa encontrada, o null si no existe
     */
    private Lid findLidByNumber(int cupNumber) {
        for (Lid lid : lids) {
            if (lid.getCupNumber() == cupNumber) {
                return lid;
            }
        }
        return null;
    }

    /**
     * Reordena la lista de tapas para que coincida con el orden actual de tazas.
     */
    private void reorderLidsAfterCups() {
        ArrayList<Lid> ordered = new ArrayList<Lid>();
        for (Cup cup : cups) {
            Lid lid = findLidByNumber(cup.getNumber());
            if (lid != null) {
                ordered.add(lid);
            }
        }
        lids = ordered;
    }

    /**
     * Elimina elementos que excedan la altura maxima o el ancho de la torre.
     */
    private void removeElementsThatDontFit() {
        int currentHeight = 0;
        ArrayList<Cup> fittingCups = new ArrayList<Cup>();
        ArrayList<Lid> fittingLids = new ArrayList<Lid>();
        for (Cup cup : cups) {
            int elementHeight = cup.getHeight();
            if (cup.hasLid()) {
                elementHeight += 1;
            }
            if (currentHeight + elementHeight <= maxHeight && cup.getNumber() <= width) {
                fittingCups.add(cup);
                if (cup.hasLid()) {
                    Lid lid = findLidByNumber(cup.getNumber());
                    if (lid != null) {
                        fittingLids.add(lid);
                    }
                }
                currentHeight += elementHeight;
            } else {
                cup.setHasLid(false);
            }
        }
        cups = fittingCups;
        lids = fittingLids;
    }

    /**
     * Genera un color aleatorio para asignar a una nueva taza.
     * 
     * @return nombre del color generado
     */
    private String generateRandomColor() {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "cyan"};
        return colors[(int)(Math.random() * colors.length)];
    }

    /**
     * Muestra un mensaje de error al usuario usando JOptionPane.
     * Solo se muestra si el simulador esta en modo visible.
     * 
     * @param message mensaje de error a mostrar
     */
    private void showError(String message) {
        if (visible) {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}