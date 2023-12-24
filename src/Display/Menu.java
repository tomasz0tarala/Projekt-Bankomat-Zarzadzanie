package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JDialog {
    private JPanel menuJPanel;
    private JButton wyswietlAkceptowaneKartyButton;
    private JButton dodajKartęButton;
    private JButton wypłaćPieniądzeButton;
    private JButton edytujLubUsuńKartęButton;
    private JButton wyjdźButton;
    private JPanel titleJPanel;
    private JLabel titleLabel;
    private JPanel middleJPanel;
    private JPanel leftMidJPanel;
    private JPanel rightMidJPanel;
    private JPanel bottomJPanel;

    public Menu() {
        setTitle("Menu");
        setContentPane(menuJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int width = 600;
        int height = 475;
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);

        wyswietlAkceptowaneKartyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ShowCards showCards = new ShowCards();
            }
        });
        wyjdźButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        dodajKartęButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCard addCard = new AddCard();
            }
        });
        edytujLubUsuńKartęButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditOrDeleteCard editOrDeleteCard = new EditOrDeleteCard();
            }
        });
        wypłaćPieniądzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WithdrawMoney withdrawMoney = new WithdrawMoney();
            }
        });
        setVisible(true);
    }

}
