package uk.ac.bradford.dungeongame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import uk.ac.bradford.dungeongame.GameEngine.TileType;

/**
 * The GameGUI class is responsible for rendering graphics to the screen to display
 * the game grid, players and monsters. The GameGUI class passes keyboard events to
 * a registered DungeonInputHandler to be handled.
 * @author prtrundl
 */
public class GameGUI extends JFrame {
    
    /**
     * The three final int attributes below set the size of some graphical elements,
     * specifically the display height and width of tiles in the dungeon and the height
     * of health bars for Entity objects in the game. Tile sizes should match the size
     * of the image files used in the game.
     */
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int HEALTH_BAR_HEIGHT = 3;
    
    /**
     * The canvas is the area that graphics are drawn to. It is an internal class
     * of the GameGUI class.
     */
    Canvas canvas;
    
    /**
     * Constructor for the GameGUI class. It calls the initGUI method to generate the
     * required objects for display.
     */
    public GameGUI() {
        initGUI();
    }
    
    /**
     * Registers an object to be passed keyboard events captured by the GUI.
     * @param i the DungeonInputHandler object that will process keybaord events to
     * make the game respond to input
     */
    public void registerKeyHandler(DungeonInputHandler i) {
        addKeyListener(i);
    }
    
    /**
     * Method to create and initialise components for displaying elements of the
     * game on the screen.
     */
    private void initGUI() {
        add(canvas = new Canvas());     //adds canvas to this frame
        setTitle("Dungeon");
        setSize(816, 615);
        setLocationRelativeTo(null);        //sets position of frame on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Method to update the graphical elements ont he screen, usually after player
     * and/or monsters have moved when a keyboard event was handled. The method
     * requires three arguments and displays corresponding information on the screen.
     * @param tiles A 2-dimensional array of TileTypes. This is the tiles of the
     * current dungeon level that should be drawn to the screen.
     * @param player An Entity object with the type EntityType.PLAYER. This object
     * is used to draw the player in the right dungeon tile and display its health.
     * null can be passed for this argument, in which case no player will be drawn.
     * @param monsters An array of EntityType.MONSTER objects that is processed to draw
     * monsters in tiles with a health bar. null can be passed for this argument in which
     * case no monsters will be drawn. Elements in the monsters array can also be null,
     * in which case nothing will be drawn.
     */
    public void updateDisplay(TileType[][] tiles, Entity player, Entity[] monsters) {
        canvas.update(tiles, player, monsters);
    }
    
}

/**
 * Internal class used to draw elements within a JPanel. The Canvas class loads
 * images from an asset folder inside the main project folder.
 * @author prtrundl
 */
class Canvas extends JPanel {

    private BufferedImage floor;
    private BufferedImage wall;
    private BufferedImage player;
    private BufferedImage monster;
    private BufferedImage stairs;
    
    TileType[][] currentTiles;  //the current 2D array of tiles to display
    Entity currentPlayer;       //the current player object to be drawn
    Entity[] currentMonsters;   //the current array of monsters to draw
    
    /**
     * Constructor that loads tile images for use in this class
     */
    public Canvas() {
        loadTileImages();
    }
    
    /**
     * Loads tiles images from a fixed folder location within the project directory
     */
    private void loadTileImages() {
        try {
            floor = ImageIO.read(new File("assets/floor.png"));
            assert floor.getHeight() == GameGUI.TILE_HEIGHT &&
                    floor.getWidth() == GameGUI.TILE_WIDTH;
            wall = ImageIO.read(new File("assets/wall.png"));
            assert wall.getHeight() == GameGUI.TILE_HEIGHT &&
                    wall.getWidth() == GameGUI.TILE_WIDTH;
            player = ImageIO.read(new File("assets/player.png"));
            assert player.getHeight() == GameGUI.TILE_HEIGHT &&
                    player.getWidth() == GameGUI.TILE_WIDTH;
            monster = ImageIO.read(new File("assets/monster.png"));
            assert monster.getHeight() == GameGUI.TILE_HEIGHT &&
                    monster.getWidth() == GameGUI.TILE_WIDTH;
            stairs = ImageIO.read(new File("assets/stairs.png"));
            assert stairs.getHeight() == GameGUI.TILE_HEIGHT &&
                    stairs.getWidth() == GameGUI.TILE_WIDTH;
        } catch (IOException e) {
            System.out.println("Exception loading images: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    
    /**
     * Updates the current graphics on the screen to display the tiles, player and monsters
     * @param t The 2D array of TileTypes representing the current level of the dungeon
     * @param player The current player object, used to draw the player and its health
     * @param mon The array of monsters to display them and their health
     */
    public void update(TileType[][] t, Entity player, Entity[] mon) {
        currentTiles = t;
        currentPlayer = player;
        currentMonsters = mon;
        repaint();
    }
    
    /**
     * Override of method in super class, it draws the custom elements for this
     * game such as the tiles, player and monsters.
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDungeon(g);
    }

    /**
     * Draws graphical elements to the screen to display the current dungeon level
     * tiles, the player and the monsters. If the tiles, player or monster objects
     * are null they will not be drawn.
     * @param g 
     */
    private void drawDungeon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (currentTiles != null) {
            for (int i = 0; i < currentTiles.length; i++) {
                for (int j = 0; j < currentTiles[i].length; j++) {
                    if (currentTiles[i][j] != null) {   //checks a tile exists
                        switch (currentTiles[i][j]) {
                            case FLOOR:
                                g2.drawImage(floor, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case WALL:
                                g2.drawImage(wall, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case STAIRS:
                                g2.drawImage(stairs, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                        }
                    }
                }
            }
        }
        if (currentMonsters != null)
            for(Entity mon : currentMonsters)
                if (mon != null) {
                    g2.drawImage(monster, mon.getX() * GameGUI.TILE_WIDTH, mon.getY() * GameGUI.TILE_HEIGHT, null);
                    drawHealthBar(g2, mon);
                }
        if (currentPlayer != null) {
            g2.drawImage(player, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
        }
    }
    
    /**
     * Draws a health bar for the given entity at the bottom of the tile that
     * the entity is located in.
     * @param g2 The graphics object to use for drawing
     * @param e The entity that the health bar will be drawn for
     */
    private void drawHealthBar(Graphics2D g2, Entity e) {
        double remainingHealth = (double)e.getHealth() / (double)e.getMaxHealth();
        g2.setColor(Color.RED);
        g2.fill(new Rectangle2D.Double(e.getX() * GameGUI.TILE_WIDTH, e.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH, GameGUI.HEALTH_BAR_HEIGHT));
        g2.setColor(Color.GREEN);
        g2.fill(new Rectangle2D.Double(e.getX() * GameGUI.TILE_WIDTH, e.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH * remainingHealth, GameGUI.HEALTH_BAR_HEIGHT));
    }
}
