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
	
    private Timer timer;
    private int dotCount;
    private long startTime;
    private final int GAME_DURATION = 30; // Duration of the game in seconds
    private DotPanel dotPanel;
    private JButton startButton;
    private JPanel timerPanel;
    private JLabel timerLabel;
    private Clip hitSound;
    
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
        
        // Timer panel above the dotPanel.
        
        timerPanel = new JPanel();
        timerLabel = new JLabel("Time Remaining: " + GAME_DURATION + "s");
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
        
    	//Disables the start button since it has already been pressed.
    	
    	startButton.setEnabled(false); // Disable the start button once the game has started
    }
    
    
    //Custom panel for the game.
    
    private class DotPanel extends JPanel {
        
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
            
            //Setting up the background image with the grey and white boxes.
            
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
    }

    //Updating the timer
    
    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        long remainingTime = GAME_DURATION - elapsedTime;

        if (remainingTime >= 0) {
            timerLabel.setText("Time Remaining: " + remainingTime + "s");
        } else {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!\nDots hit: " + dotCount);
            dotPanel.removeMouseListener(dotPanel.getMouseListeners()[0]);
            startButton.setEnabled(true); // Enable the start button
        }
    }
    
    private void playHitSound() {
        if (hitSound != null) {
            hitSound.setFramePosition(0);  // Rewind the sound to the beginning
            hitSound.start();  // Play the sound
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
                File imageFile = new File("C:\\Users\\nikol\\Desktop\\targetimage.png"); // Replace with the actual path to your image file
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

        public boolean isHit(int clickX, int clickY) {
            int dotSize = dotPanel.getDotSize();
            return clickX >= x && clickX <= x + dotSize && clickY >= y && clickY <= y + dotSize;
        }
    }
}



