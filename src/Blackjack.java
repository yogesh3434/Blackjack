import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import javax.swing.*;

public class Blackjack {
    private class Card {
        String value;
        String type;
        boolean isHidden;

        Card(String value, String type) {
            this(value, type,false);
        }
        Card(String value, String type, boolean isHidden) {
            this.value = value;
            this.type = type;
            this.isHidden = isHidden;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { //A J Q K
                if (value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); //2-10
        }
        public static int calculateScore(List<Card> cards) {
            int score = 0;
            int aceCount = 0;
        
            for (Card card : cards) {
                if (card.isAce()) {
                    aceCount++;
                    score += 11;
                } else {
                    score += card.getValue();
                }
            }
        
            while (score > 21 && aceCount > 0) {
                score -= 10;
                aceCount--;  
            }
        
            return score;
        }        

        public boolean isAce() {
            return value == "A";
        }

        public String getImagePath() {
            return "./cards/"+toString()+ ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random(); //shuffle deck

    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;

    //player
    ArrayList<Card> playerHand;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cardWidth = 110; //ratio should 1/1.4
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            try {

                //draw dealer's hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    var imagePath = card.isHidden ? getClass().getResource("./cards/BACK.png") : getClass().getResource(card.getImagePath());
                    Image cardImg = new ImageIcon(imagePath).getImage();
                    g.drawImage(cardImg, 20+ (cardWidth+5)*i, 20, cardWidth, cardHeight, null);
                }

                //draw player's hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight,null);
                }

                if (!stayButton.isEnabled()) {
                    var dealerScore = Card.calculateScore(dealerHand);
                    var playerScore = Card.calculateScore(playerHand);
                    System.out.println("STAY: ");
                    System.out.println(dealerScore);
                    System.out.println(playerScore);

                    String message = "";
                    if (playerScore > 21) {
                        message = "You Bust!";
                    }
                    else if (dealerScore > 21) {
                        message = "You Win!";
                    }
                    //both you and dealer <= 21
                    else if (playerScore == dealerScore) {
                        message = "Push!";
                    }
                    else if (playerScore > dealerScore) {
                        message = "You Win!";
                    }
                    else if (playerScore < dealerScore) {
                        message = "You Lose!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton resetGame = new JButton("RESET");

    Blackjack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        resetGame.setFocusable(false);
        buttonPanel.add(resetGame);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                Card card = deck.remove(deck.size()-1);
                
                playerHand.add(card);
                if(Card.calculateScore(playerHand)>21)
                {
                    dealerHand.get(0).isHidden = false;
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                    gamePanel.repaint();
                    JOptionPane.showMessageDialog(frame, "You bust! Dealer wins!");

                }
                else{
                    gamePanel.repaint();
                }
                
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                dealerHand.get(0).isHidden = false;

                while (Card.calculateScore(dealerHand) < 17) {
                    Card card = deck.remove(deck.size()-1);
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        resetGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Rebuild the deck
                buildDeck();
                
                // Shuffle the deck
                shuffleDeck();
        
                // Clear the hands
                dealerHand.clear();
                playerHand.clear();
        
                // Reset buttons
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
        
                // Start a new game
                startGame();
        
                // Repaint the panel to reflect changes
                gamePanel.repaint();
            }
        });
        gamePanel.repaint();
    }

    public void startGame() {
        //deck
        buildDeck();
        shuffleDeck();
    
        //dealer
        dealerHand = new ArrayList<Card>();
    
        hiddenCard = deck.remove(deck.size()-1); //remove card at last index
        hiddenCard.isHidden = true;
        dealerHand.add(hiddenCard);
    
        dealerHand.add(deck.remove(deck.size()-1));
    
        System.out.println("DEALER:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
    
        //player
        playerHand = new ArrayList<Card>();
    
        for (int i = 0; i < 2; i++) {
            playerHand.add(deck.remove(deck.size()-1));
        }
    
        System.out.println("PLAYER: ");
        System.out.println(playerHand);
        
        // Check if player has blackjack
        int playerScore = Card.calculateScore(playerHand);
        if (playerScore == 21) {
            hitButton.setEnabled(false);
            stayButton.setEnabled(false);
            dealerHand.get(0).isHidden = false; // Reveal the dealer's hidden card
            gamePanel.repaint();
            JOptionPane.showMessageDialog(frame, "Blackjack! You win!");
        }
    }
    


    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }

        System.out.println("BUILD DECK:");
        System.out.println(deck);
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);
    }
}