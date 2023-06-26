package reactiontime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.io.IOException;
import javax.sound.sampled.*;
import java.io.File;



public class ReactionTimeGame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Timer timer;
    private int dotCount;
    private long startTime;
    private final int GAME_DURATION = 30; // Duration of the game in seconds
    private DotPanel dotPanel;
    
    
    public ReactionTimeGame() {
        setTitle("Reaction Time Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new BorderLayout());

        dotPanel = new DotPanel();
        add(dotPanel, BorderLayout.CENTER);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });
        
        
        
    }

    public void startGame() {
        dotCount = 0;
        startTime = System.currentTimeMillis();
        dotPanel.showNextDot();
        timer.start();
    }

    private class DotPanel extends JPanel {
        
		private static final long serialVersionUID = 1L;
		private Random random;
        private int dotSize;
        private Dot currentDot;

        public DotPanel() {
        	
            random = new Random();
            dotSize = 30; // Adjust the size of the dot as per your preference
            setBackground(new Color(0,128,128));
            setPreferredSize(new Dimension(1000, 1000));
            setLayout(null);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    hitDot(e);
                }
            });
        
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
            g.setColor(Color.RED);
            if (currentDot != null && currentDot.isVisible()) {
                g.fillOval(currentDot.getX(), currentDot.getY(), dotSize, dotSize);
            }
        }
    }

    private void hitDot(MouseEvent e) {
        if (dotPanel.currentDot != null && dotPanel.currentDot.isHit(e.getX(), e.getY())) {
            dotPanel.showNextDot();
        }
        
        
    }

    private void updateTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000;
        long remainingTime = GAME_DURATION - elapsedTime;

        if (remainingTime >= 0) {
            setTitle("Reaction Time Game - Time: " + remainingTime + "s");
        } else {
            timer.stop();
            dotPanel.setVisible(false);
            JOptionPane.showMessageDialog(this, "Game Over!\nDots hit: " + dotCount);
            dispose();
        }
    }

    private class Dot {
        private int x;
        private int y;
        private boolean visible;

        public Dot(int x, int y) {
            this.x = x;
            this.y = y;
            this.visible = false;
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
            return clickX >= x && clickX <= x + dotSize &&
                    clickY >= y && clickY <= y + dotSize;
        }
    }

    
}


