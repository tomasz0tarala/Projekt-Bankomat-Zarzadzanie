package Display;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditOrDeleteCard extends JDialog{
    private JTextField newNameField;
    private JPasswordField newPasswordField;
    private JTextField newBalanceField;
    private JLabel titleLabel;
    private JPanel topJPanel;
    private JLabel leftTitleLabel;
    private JLabel middleTitleLabel;
    private JLabel rightTitleLabel;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTable cardsTable;
    private JPanel editOrDeleteJPanel;
    private JLabel iconLabel;
    private JPanel bottomJPanel;
    private JPanel middleBottomJPanel;
    private JPanel middleTopJPanel;
    private JScrollPane scrollPane;

    public EditOrDeleteCard() {
        setTitle("Edytuj lub usuń kartę");
        setContentPane(editOrDeleteJPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int width = 600;
        int height = 475;
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
}
