package simulator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Display extends JFrame implements ActionListener,WindowListener
{
    JPanel panel = null;
    JButton address_label = new JButton("ADDRESS");
    JButton content_label = new JButton("CONTENT");
    JButton[] address=null;
    JButton[] content=null;
    int size=0;

    public Display(String workspace[][])
    {
        super("Display");
        size=workspace.length;

        panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(600,(size+2)*20));
        panel.setSize(600, 600);
        panel.revalidate();

        Font font1 = new Font("Arial", Font.BOLD, 15);
        Font font2 = new Font("Arial", Font.PLAIN, 12);

        address_label.setFont(font1);
        address_label.setBackground(Color.black);
        address_label.setForeground(Color.white);
        address_label.setBounds(0, 0, 300, 40);
        content_label.setFont(font1);
        content_label.setBackground(Color.black);
        content_label.setForeground(Color.white);
        content_label.setBounds(300, 0, 300, 40);

        panel.add(address_label);
        panel.add(content_label);

        address = new JButton[size];
        content = new JButton[size];

        for(int i=0;i<size;i++)
        {
            address[i]=new JButton(workspace[i][0]);
            address[i].setBounds(0,40+i*20,300,20);
            address[i].setFont(font2);
            address[i].setForeground(Color.black);
            address[i].addActionListener(this);
            panel.add(address[i]);

            content[i]=new JButton(workspace[i][1]);
            content[i].setBounds(300,40+i*20,300,20);
            content[i].setFont(font2);
            content[i].setForeground(Color.black);
            content[i].addActionListener(this);
            panel.add(content[i]);

            if(i%2 == 1)
            {
                address[i].setBackground(Color.cyan);
                content[i].setBackground(Color.cyan);
            }
            else
            {
                address[i].setBackground(Color.white);
                content[i].setBackground(Color.white);
            }
        }

        JScrollPane scrollpanel=new JScrollPane(panel);
        scrollpanel.setSize(600,600);
        scrollpanel.setPreferredSize(new Dimension(600,(size+2)*20));
        this.getContentPane().add(scrollpanel);

        this.setSize(new Dimension (600, 600));
        this.setVisible (true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.addWindowListener(this);
    }

    public void windowOpened(WindowEvent e){}

    public void windowClosing(WindowEvent e)
    {
        this.dispose();
    }

    public void windowClosed(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void actionPerformed(ActionEvent e)
    {
        for(int i=0;i<size;i++)
            if(e.getSource()==address[i] || e.getSource()==content[i])
            {
                JOptionPane.showMessageDialog(null, "ADDRESS : "+address[i].getText()+"\nCONTENT : "+content[i].getText(), "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
    }
}
