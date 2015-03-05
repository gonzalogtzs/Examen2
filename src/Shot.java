
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author http://zetcode.com/
 */
public class Shot extends Sprite {

    private String shot = "laser.png"; // Asignandole imagen al disparo
    private final int H_SPACE = 6; // horizontal
    private final int V_SPACE = 1; // vertical

    public Shot() {
    }

    public Shot(int x, int y) {
        
        //Asignandole a imageicon las propiedades de la imagen
        ImageIcon ii = new ImageIcon(this.getClass().getResource(shot));
        setImage(ii.getImage());
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}