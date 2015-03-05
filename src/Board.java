
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author http://zetcode.com/
 */
public class Board extends JPanel implements Runnable, Commons { 

    private Dimension d; //Dimension lo que se va a mostrar en pantalla
    private ArrayList aliens; // Declarando lista de aliens
    private Player player; // Declarando objeto de la clase jugador
    private Shot shot; // Declarando disparo de la clase shot
    private boolean bPause = true; //declarando pausa del juego
    private boolean bCredito = false; //Declarando creditos
    private boolean bInstrucciones = false;//Declarando instrucciones
    private int iCounterPause = 0; //Declarando contador de pausa
    private Graphics graGraficaApplet; //Declarando graficador
    private final SoundClip sndSonidoDisparo;//Sonido para disparo
    private final SoundClip sndSonidoPerder;//Sonido para perder
    private final SoundClip sndSonidoIncio;//Sonido de inicio
    private Image imaImagenApplet;// Imagen para dibujar
    private boolean bStart = true;// Variable para empezar el juego

    private int alienX = 150;//Posicion x de alien
    private int alienY = 5;//Posicion y de alien
    private int direction = -1;//Direccion
    private int deaths = 0;//Muertes

    private boolean ingame = true;//Boleana para el estado del juego
    private final String expl = "Explosion2.jpg";//Inicializando explosion
    private final String alienpix = "alien.png";//Inicializando alien
    private String message = "Haz Perdido! Lo siento!";//Mensaje de juego perdido
    

    private Thread animator;

    public Board() 
    {
        //Inicializando imagen de fondo
        URL urlImagenFondo = this.getClass().getResource("back.gif");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage
        (urlImagenFondo);
        sndSonidoPerder = new SoundClip("choque.wav");//Inicializando sonido perder
        sndSonidoDisparo = new SoundClip("sonido.wav");//Sonido disparo     
        sndSonidoIncio = new SoundClip("inicios.mp3");//Sonido inicio
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGTH);//Dimension del frame
        setBackground(Color.black);//Color de fondo

        gameInit();
        setDoubleBuffered(true);
    }

    public void addNotify() {
        super.addNotify();
        gameInit();
    }

    public void gameInit() {

        aliens = new ArrayList(); //Lista de aliens

        //Agregando los objetos aliens a la lista alien
        ImageIcon ii = new ImageIcon(this.getClass().getResource(alienpix));

        for (int i=0; i < 4; i++) {
            for (int j=0; j < 6; j++) {
                Alien alien = new Alien(alienX + 18*j, alienY + 18*i);
                alien.setImage(ii.getImage());
                aliens.add(alien);
            }
        }

        player = new Player();
        shot = new Shot();

        if (animator == null || !ingame) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void drawAliens(Graphics g) 
    {
        Iterator it = aliens.iterator();

        while (it.hasNext()) {
            Alien alien = (Alien) it.next();

            //Aparicion de alien
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            //Muerte de alien
            if (alien.isDying()) {
                alien.die();
            }
        }
    }

    public void drawPlayer(Graphics g) {

        //Aparicion de jugador
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        //Muerte de jugador
        if (player.isDying()) {
            player.die();
            ingame = false;
        }
    }

    //Dibujando el disparo
    public void drawShot(Graphics g) {
        if (shot.isVisible())
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
    }

    //Dibujando bombas de los aliens
    public void drawBombing(Graphics g) {

        Iterator i3 = aliens.iterator();

        while (i3.hasNext()) {
            Alien a = (Alien) i3.next();

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this); 
            }
        }
    }

    public void paint(Graphics graGrafico)
    {
        
      super.paint(graGrafico);

      graGrafico.setColor(Color.black);
      
      graGrafico.setColor(Color.green);
      
      if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }
      
      if (ingame) {
          //Creditos del juego
            if (bCredito) {
              graGrafico.setColor(Color.white);
              graGrafico.drawString("Este juego fue creado por:", 20, 20);
              graGrafico.setColor(Color.red);
              graGrafico.drawString("Gonzalo Gutierrez", 20, 50);
              graGrafico.setColor(Color.orange);
              graGrafico.drawString("Isaac Siso", 20, 70);
             }
            //Instrucciones
            else if (bInstrucciones) {
              graGrafico.setColor(Color.white);
              graGrafico.drawString("Instrucciones:", 20, 20);
              graGrafico.setColor(Color.red);
              graGrafico.drawString("Presiona 'R' para poner los creditos", 
                      20, 50);
              graGrafico.drawString("Despresiona 'R' para quitar los creditos", 
                      20, 70);
              graGrafico.setColor(Color.orange);
              graGrafico.drawString("Presiona 'P' para poner pausa", 20, 90);
              graGrafico.drawString("Presiona otra vez 'P' para quitar pausa", 
                      20, 110);
             }
            //Start
            else if (bStart) {
                
                URL urlImagenStart = this.getClass().getResource("Didi.png");
                Image imaImagenStart = Toolkit.getDefaultToolkit().getImage
                (urlImagenStart);
                graGraficaApplet.drawImage(imaImagenStart, 0, 0, 500, 500, this);
                graGrafico.drawImage (imaImagenApplet, 0, 0, this);
            }
            
            else {
                URL urlImagenFondo = this.getClass().getResource("back.gif");
                Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage
                (urlImagenFondo);
                graGraficaApplet.drawImage(imaImagenFondo, 0, 0, 500, 500, this);

                graGrafico.drawImage (imaImagenApplet, 0, 0, this);

            graGrafico.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawAliens(graGrafico);
            drawPlayer(graGrafico);
            drawShot(graGrafico);
            drawBombing(graGrafico);
            }
      }

      Toolkit.getDefaultToolkit().sync();
      graGrafico.dispose();
    }

    public void gameOver()
    {
 
        Graphics g = this.getGraphics();
        
        URL urlImagenFondo = this.getClass().getResource("back.gif");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage
        (urlImagenFondo);
        graGraficaApplet.drawImage(imaImagenFondo, 0, 0, 500, 500, this);
        g.drawImage (imaImagenApplet, 0, 0, this);  
        //Detalles de la tipografia
        Font small = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metr = this.getFontMetrics(small);
        g.setColor(Color.red);
        g.setFont(small);
        g.drawString(message, (500 - metr.stringWidth(message))/2, 
            120/2);
        g.setColor(Color.orange);
        g.drawString("Didi esta muy triste!", (500 - metr.stringWidth
        ("Didi esta muy triste!"))/2,200/2);
    }

    public void animationCycle()  {

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            ingame = false;
            message = "Felicidades! Ganaste!";
        }

        // player

        player.act();

        // Colision 
        if (shot.isVisible()) {
            Iterator it = aliens.iterator();
            int shotX = shot.getX();
            int shotY = shot.getY();

            while (it.hasNext()) {
                Alien alien = (Alien) it.next();
                int alienX = alien.getX();
                int alienY = alien.getY();

                if (alien.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX) && 
                        shotX <= (alienX + ALIEN_WIDTH) &&
                        shotY >= (alienY) &&
                        shotY <= (alienY+ALIEN_HEIGHT) ) {
                            ImageIcon ii = 
                                new ImageIcon(getClass().getResource(expl));
                            alien.setImage(ii.getImage());
                            alien.setDying(true);
                            deaths++;
                            shot.die();
                        }
                }
            }

            int y = shot.getY();
            y -= 4;
            if (y < 0)
                shot.die();
            else shot.setY(y);
        }

        // aliens

         Iterator it1 = aliens.iterator();

         while (it1.hasNext()) {
             Alien a1 = (Alien) it1.next();
             int x = a1.getX();

             if (x  >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                 direction = -1;
                 Iterator i1 = aliens.iterator();
                 while (i1.hasNext()) {
                     Alien a2 = (Alien) i1.next();
                     a2.setY(a2.getY() + GO_DOWN);
                 }
             }

            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;

                Iterator i2 = aliens.iterator();
                while (i2.hasNext()) {
                    Alien a = (Alien)i2.next();
                    a.setY(a.getY() + GO_DOWN);
                }
            }
        }

        Iterator it = aliens.iterator();

        while (it.hasNext()) {
            Alien alien = (Alien) it.next();
            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > GROUND - ALIEN_HEIGHT) {
                    ingame = false;
                    message = "Invasion!";
                }

                alien.act(direction);
            }
        }

        // bombs

        Iterator i3 = aliens.iterator();
        Random generator = new Random();

        while (i3.hasNext()) {
            int shot = generator.nextInt(15);
            Alien a = (Alien) i3.next();
            Alien.Bomb b = a.getBomb();
            if (shot == CHANCE && a.isVisible() && b.isDestroyed()) {

                b.setDestroyed(false);
                b.setX(a.getX());
                b.setY(a.getY());   
            }

            int bombX = b.getX();
            int bombY = b.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !b.isDestroyed()) {
                if ( bombX >= (playerX) && 
                    bombX <= (playerX+PLAYER_WIDTH) &&
                    bombY >= (playerY) && 
                    bombY <= (playerY+PLAYER_HEIGHT) ) {
                        sndSonidoPerder.play();
                        ImageIcon ii = 
                            new ImageIcon(this.getClass().getResource(expl));
                        player.setImage(ii.getImage());
                        player.setDying(true);
                        b.setDestroyed(true);;
                    }
            }
            
            if (!b.isDestroyed()) {
                b.setY(b.getY() + 1);   
                if (b.getY() >= GROUND - BOMB_HEIGHT) {
                    b.setDestroyed(true);
                    
                }
            }
        }
    }

    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (ingame) {
            repaint();
            
            animationCycle();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) 
                sleep = 2;
            try {
                Thread.sleep(sleep);
                
                if (bPause) {
                    while (iCounterPause == 1) {
                      Thread.sleep (20);  
                    }
                    iCounterPause = 1;
                }
                
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            beforeTime = System.currentTimeMillis();
        }
        gameOver();
    }

    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
            
            if(e.getKeyCode() == KeyEvent.VK_P) {    
                if(bPause){
                bPause = false;
                iCounterPause = 0;
                }
                else{
                    bPause = true;
                    iCounterPause = 0;
                }
            }
                
            if(e.getKeyCode() == KeyEvent.VK_I) {    
                bInstrucciones = false;
                bPause = false;
                iCounterPause = 0;
          }
            
            if(e.getKeyCode() == KeyEvent.VK_R) {    
                bCredito = false;
                bPause = false;
                iCounterPause = 0;
            }
            
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_G){
            try {
                grabarJuego();
            } catch (IOException ex) {
                Logger.getLogger(SpaceInvaders.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Al presionar C se carga el juego
        if (e.getKeyCode() == KeyEvent.VK_C) {
            try {
                cargarJuego();
            } catch (IOException ex) {
                Logger.getLogger(SpaceInvaders.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
          
          if(e.getKeyCode() == KeyEvent.VK_R) {    
                bCredito = true;
                bPause = true;
                iCounterPause = 0;
          }
          if(e.getKeyCode() == KeyEvent.VK_S) {    
                bStart = false;
                bPause = false;
                iCounterPause = 0;
          }
          if(e.getKeyCode() == KeyEvent.VK_I) {    
                bInstrucciones = true;
                bPause = true;
                iCounterPause = 0;
          }

          player.keyPressed(e);

          int x = player.getX();
          int y = player.getY();

          if (ingame)
          {
            if (e.isAltDown()) {
                sndSonidoDisparo.play();
                if (!shot.isVisible())
                    shot = new Shot(x, y);
                    
            }
          }
        }
    }
       
    public void grabarJuego() throws IOException {
        PrintWriter fileOut = new PrintWriter(new FileWriter("gameData.txt"));

        //se guardan variables generales
        fileOut.println(bPause); 
        fileOut.println(player.getX()); //Se guarda x de lolita
        fileOut.println(player.getY()); //Se guarda y
        fileOut.println(shot.getX());
        fileOut.println(shot.getY());
        fileOut.close();//Se cierra el archivo
    }
    public void cargarJuego() throws IOException {
                                                                  
        BufferedReader fileIn;
        try {
                fileIn = new BufferedReader(new FileReader("gameData.txt"));
        } catch (FileNotFoundException e){
                File puntos = new File("gameData.txt");
                PrintWriter fileOut = new PrintWriter(puntos);
                fileOut.println("100,demo");
                fileOut.close();
                fileIn = new BufferedReader(new FileReader("gameData.txt"));
        }
        
        String aux = fileIn.readLine();
        bPause = (Boolean.parseBoolean(aux)); 
       
        aux = fileIn.readLine();
        player.setX((Integer.parseInt(aux))); 
        
        aux = fileIn.readLine();
        player.setY((Integer.parseInt(aux)));
        
        aux = fileIn.readLine();
        shot.setX((Integer.parseInt(aux))); 
        
        aux = fileIn.readLine();
        shot.setY((Integer.parseInt(aux)));
        
        fileIn.close();
    }
}
