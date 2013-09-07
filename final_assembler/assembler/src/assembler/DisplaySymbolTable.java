package assembler;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.*;

public class DisplaySymbolTable extends JFrame implements ActionListener,WindowListener
{
    JPanel panel = null;
    JButton name_label = new JButton("NAME");
    JButton value_label = new JButton("VALUE");
    JButton length_label = new JButton("LENGTH");
    JButton reloc_label = new JButton("RELOC");
    JButton[] name=null;
    JButton[] value=null;
    JButton[] length=null;
    JButton[] reloc=null;

    int size=0;

    public DisplaySymbolTable(String symbolfile, int s)
    {
        super("Symbol Table");
        size=s;

        panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(600,(size+2)*20));
        panel.setSize(600, 600);
        panel.revalidate();

        Font font1 = new Font("Arial", Font.BOLD, 15);
        Font font2 = new Font("Arial", Font.PLAIN, 12);

        name_label.setFont(font1);
        name_label.setBackground(Color.black);
        name_label.setForeground(Color.white);
        name_label.setBounds(0, 0, 200, 40);
        value_label.setFont(font1);
        value_label.setBackground(Color.black);
        value_label.setForeground(Color.white);
        value_label.setBounds(200, 0, 200, 40);
        length_label.setFont(font1);
        length_label.setBackground(Color.black);
        length_label.setForeground(Color.white);
        length_label.setBounds(400, 0, 100, 40);
        reloc_label.setFont(font1);
        reloc_label.setBackground(Color.black);
        reloc_label.setForeground(Color.white);
        reloc_label.setBounds(500, 0, 100, 40);

        panel.add(name_label);
        panel.add(value_label);
        panel.add(length_label);
        panel.add(reloc_label);

        name = new JButton[size];
        value = new JButton[size];
        length = new JButton[size];
        reloc = new JButton[size];

        try
        {
            Scanner sc = new Scanner(new FileInputStream(new File(symbolfile)));

            for(int i=0;i<size;i++)
            {
                name[i]=new JButton(sc.next());
                name[i].setBounds(0,40+i*20,200,20);
                name[i].setFont(font2);
                name[i].setForeground(Color.black);
                name[i].addActionListener(this);
                panel.add(name[i]);

                value[i]=new JButton(sc.next());
                value[i].setBounds(200,40+i*20,200,20);
                value[i].setFont(font2);
                value[i].setForeground(Color.black);
                value[i].addActionListener(this);
                panel.add(value[i]);

                length[i]=new JButton(sc.next());
                length[i].setBounds(400,40+i*20,100,20);
                length[i].setFont(font2);
                length[i].setForeground(Color.black);
                length[i].addActionListener(this);
                panel.add(length[i]);

                reloc[i]=new JButton(sc.next());
                reloc[i].setBounds(500,40+i*20,100,20);
                reloc[i].setFont(font2);
                reloc[i].setForeground(Color.black);
                reloc[i].addActionListener(this);
                panel.add(reloc[i]);

                if(i%2 == 1)
                {
                    name[i].setBackground(Color.cyan);
                    value[i].setBackground(Color.cyan);
                    length[i].setBackground(Color.cyan);
                    reloc[i].setBackground(Color.cyan);
                }
                else
                {
                    name[i].setBackground(Color.white);
                    value[i].setBackground(Color.white);
                    length[i].setBackground(Color.white);
                    reloc[i].setBackground(Color.white);
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
        catch(FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "Symbol Table File not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
        catch(NoSuchElementException e)
        {
            JOptionPane.showMessageDialog(null, "Symbol Table File incomplete!", "Error Message", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
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
            if(e.getSource()==name[i] || e.getSource()==value[i] || e.getSource()==length[i] || e.getSource()==reloc[i])
                JOptionPane.showMessageDialog(null, "SYMBOL NAME : "+name[i].getText()+"\nVALUE : "+value[i].getText()+"\nLENGTH : "+length[i].getText()+"\nRELOCATABLE : "+reloc[i].getText(), "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
