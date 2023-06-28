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
import java.io.File;
import java.io.IOException;

//Creating the class for the game

public class ReactionTimeGame extends JFrame {
	
    private Timer timer;
    private int dotCount;
    private long startTime;
    private final int GAME_DURATION = 30; // Duration of the game in seconds
    private DotPanel dotPanel;
    private JButton startButton;
    private JPanel timerPanel;
    private JLabel timerLabel;
    private Clip hitSound;
    
    
    public ReactionTimeGame() {
        setTitle("Reaction Time Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new BorderLayout());

        dotPanel = new DotPanel();
        add(dotPanel, BorderLayout.CENTER);
        
        
        
        // Start button
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(10,50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton, BorderLayout.NORTH);
        
        // Timer panel
        timerPanel = new JPanel();
        timerLabel = new JLabel("Time: " + GAME_DURATION + "s");
        timerPanel.setPreferredSize(new Dimension(10,40));
        timerPanel.add(timerLabel);
        add(timerPanel, BorderLayout.SOUTH);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });
        
        
        try {
            File soundFile = new File("C:\\Users\\nikol\\Desktop\\boomsound.au");  
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            hitSound = AudioSystem.getClip();
            hitSound.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        
    }
   

    public void startGame() {
        dotCount = 0;
        startTime = System.currentTimeMillis();
        dotPanel.showNextDot();
        timer.start();
        startButton.setEnabled(false); // Disable the start button once the game has started
    }

    private class DotPanel extends JPanel {
        private Random random;
        private int dotSize;
        private Dot currentDot;
        private BufferedImage backgroundImage;
        public DotPanel() {
            random = new Random();
            dotSize = 32; // Adjust the size of the dot as per your preference
            
            
            setPreferredSize(new Dimension(1000, 1000));
            setLayout(null);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    hitDot(e);
                }
            });
            
            try {
                File imageFile = new File("C:\\Users\\nikol\\Desktop\\blackandwhite.jpg");
                backgroundImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public int getDotSize() {
            return dotSize;
        }

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

    private void hitDot(MouseEvent e) {
        if (dotPanel.currentDot != null && dotPanel.currentDot.isHit(e.getX(), e.getY())) {
            dotPanel.showNextDot();
            playHitSound();
        }
    }

    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        long remainingTime = GAME_DURATION - elapsedTime;

        if (remainingTime >= 0) {
            timerLabel.setText("Time: " + remainingTime + "s");
        } else {
            timer.stop();
            dotPanel.setVisible(false);
            JOptionPane.showMessageDialog(this, "Game Over!\nDots hit: " + dotCount);
            dispose();
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



