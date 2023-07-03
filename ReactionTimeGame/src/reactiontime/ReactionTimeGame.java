package reactiontime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//Creating the class for the game

public class ReactionTimeGame extends JFrame {
	
	//Variable initialization.
	
    
	private static final long serialVersionUID = 1L;
	private Timer timer;
    private int dotCount;
    private long startTime;
    
    // Duration of the game in seconds.
    
    private final int GAME_DURATION = 30; 
    private DotPanel dotPanel;
    private JButton startButton;
    private JPanel timerPanel;
    private JLabel timerLabel;
    private Clip hitSound;
    private int highScore=0;
    private boolean gameStarted;
    
    //Constructor.
    public ReactionTimeGame() {
    	
    	//Setting the title and the size of the opening frame.

    	setTitle("Reaction Time Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new BorderLayout());
        
        //Creating an object for the dotPanel,which is the image background.
        
        dotPanel = new DotPanel();
        add(dotPanel, BorderLayout.CENTER);
        
        // Start button for the game.
        
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(10,50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton, BorderLayout.NORTH);
        
        // Timer panel and highScore.
        
        timerPanel = new JPanel();
        timerLabel = new JLabel("Time Remaining: " + GAME_DURATION + "s"+ "                       HighScore:"+highScore);
        timerPanel.setPreferredSize(new Dimension(10,40));
        timerPanel.add(timerLabel);
        add(timerPanel, BorderLayout.SOUTH);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });
        
        //Implementing a sound ,which will be used each time the target is hit.
        
        try {
            File soundFile = new File("C:\\Users\\nikol\\Desktop\\boomsound.au");  
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            hitSound = AudioSystem.getClip();
            hitSound.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        
    }
   
    //This method is called one time when the game starts.

    public void startGame() {
    	
    	
        //Starts counting the valid hits.
    	
    	dotCount = 0;
        
    	//Stores the time which the game started,and calculates the time passed based on GAME_DURATION.
    	
    	startTime = System.currentTimeMillis();
        
    	//Shows the first dot.
    	
    	dotPanel.showNextDot();
        
    	//Starts the timer.
    	
    	timer.start();
        
    	// Disable the start button once the game has started.
    	
    	startButton.setEnabled(false); 
    	
    	//Set the game status to true
    	
    	gameStarted=true;
    	
    	//Call the mouse listener to enable it.
    	
    	MouseListener();
    }
    
    
    //Custom panel for the game.
    
    private class DotPanel extends JPanel {
        
    	private static final long serialVersionUID = 1L;
    	
    	//Instance of the random class,used for generating random numbers.		

		private Random random;
        
    	//Integer for the dotSize.
    	
    	private int dotSize;
        
    	//Instance for the dot we currently are being displayed while playing.
    	
    	private Dot currentDot;
        
    	//A buffered image variable that holds the background image.
    	
    	private BufferedImage backgroundImage;
        
    	
    	//Method for each time a new dot appears.
    	
    	public DotPanel() {
    		
    		//Random coordinations for the new dot appear.
    		
            random = new Random();
            dotSize = 32; 
            
            
            //Adding MouseListener so each click PRESS is recognized.

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    hitDot(e);
                }
            });
            
            //Setting up the background image with the black squares.
            
            try {
                File imageFile = new File("C:\\Users\\nikol\\Desktop\\blacksquares.jpg");
                backgroundImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
            
        }
        
    	//Getter method for the dotSize;
    	
        public int getDotSize() {
            return dotSize;
        }

        //Method that shows the next dot,after the current one is hit.
        
        public void showNextDot() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (currentDot != null) {
                currentDot.setVisible(false);
                currentDot = null;
            }

            if (dotCount < 50) {
                int x = random.nextInt(panelWidth - dotSize);
                int y = random.nextInt(panelHeight - dotSize);
                currentDot = new Dot(x, y);
                currentDot.setVisible(true);
                dotCount++;
            }

            repaint();
        }
        
        //Paint method
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw the background image
            
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // Draw the dot image
            
            if (currentDot != null && currentDot.isVisible()) {
                g.drawImage(currentDot.getImage(), currentDot.getX(), currentDot.getY(), dotSize, dotSize, this);
            }
        }

        
        
    }
    
    //Each time a dot is hit,it will show the next one and the sound will be played.
    
    private void hitDot(MouseEvent e) {
        if (dotPanel.currentDot != null && dotPanel.currentDot.isHit(e.getX(), e.getY())) {
            dotPanel.showNextDot();
            playHitSound();
        }
        if (dotCount>highScore) {
        	highScore=dotCount;        	
        }
    }

    //Updating the timer
    
    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        long remainingTime = GAME_DURATION - elapsedTime;

        if (remainingTime >= 0) {
            timerLabel.setText("Time Remaining: " + remainingTime + "s"+ "                       HighScore:"+highScore);
        } else {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!\nDots hit: " + dotCount);
            
            //Remove the mouse listener.
            
            dotPanel.removeMouseListener(dotPanel.getMouseListeners()[0]);
            
            //Variable for the mouseListener.
            
            gameStarted=false;
            
            //Call MouseListener
            
            MouseListener();
               
            // Enable the start button
         
            startButton.setEnabled(true); 
            
            
        }
    }
    
    private void playHitSound() {
        if (hitSound != null) {
        	// Rewind the sound to the beginning
        	
            hitSound.setFramePosition(0);  
            
            // Play the sound
            
            hitSound.start();  
        }
    }

    

    private class Dot {
        private int x;
        private int y;
        private boolean visible;
        private Image image;
        
        public Dot(int x, int y) {
            this.x = x;
            this.y = y;
            this.visible = false;
            
            try {
                File imageFile = new File("C:\\Users\\nikol\\Desktop\\targetimage.png"); 
                image = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public Image getImage() {
            return image;
        }
        
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        
        //Method to check if the click on the X and Y coordinates,match the coordinates of the dot that appeared.
        
        public boolean isHit(int clickX, int clickY) {
        	
        	//Get the size of the dot.
            
        	int dotSize = dotPanel.getDotSize();
            
            //Return true,if the clicks on X and Y are within the coordination limits.Then the hit counts.Otherwise it does not count.
            
        	return clickX >= x && clickX <= x + dotSize && clickY >= y && clickY <= y + dotSize;
        }
    }
    
    //Checks whether the game started or not,so it enables or disabled the mouse on the dotPanel.
    
    private void MouseListener() {
        if (gameStarted) {
            dotPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    hitDot(e);
                }
            });
        }
        else {
        	dotPanel.removeMouseListener(dotPanel.getMouseListeners()[0]);
        }
    }
}



