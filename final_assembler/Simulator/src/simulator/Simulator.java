package simulator;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Simulator extends JFrame implements ActionListener
{

    JPanel panel = null;
    JButton loadfile=null, displaycontent=null, execute=null, nextline=null, cancel=null;
    JRadioButton stepmode=null, quickmode=null, burstmode=null;
    ButtonGroup buttonGroup = new ButtonGroup();
    JTextField regContents[] = null, flagContent[] = null, location_counter = null;
    JLabel registerLabel = null, registers[] = null, flags[] = null, loc_counterLabel = null;
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    String workspace[][] = null,name = null, initial_add = null, exec_add = null;
    String ESD_table[][][] = new String[3][50][4];
    int segment_length = 0, line = 0;
    int base_register[] = new int[3];

    int ESD_index=0;
    
    public Simulator()
    {
        super("Simulator");

        panel = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                g.drawImage(new ImageIcon("C:/Users/Vivek/Desktop/Simulator/simulatorbackground.jpg").getImage(), 0, 0, null);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(dimension);
        panel.setBounds(0,0,dimension.width,dimension.height);
        panel.revalidate();

        Font font1 = new Font("Arial", Font.BOLD, 20);
        Font font2 = new Font("Arial", Font.BOLD, 12);

        loadfile = new JButton("LOAD FILE");
        loadfile.setBounds(75, 100, 300, 40);
        loadfile.setFont(font1);
        panel.add(loadfile);
        loadfile.addActionListener(this);

        displaycontent = new JButton("DISPLAY CONTENT");
        displaycontent.setBounds(75, 200, 300, 40);
        displaycontent.setFont(font1);
        panel.add(displaycontent);
        displaycontent.addActionListener(this);

        stepmode = new JRadioButton("STEP MODE");
        stepmode.setBounds(75, 300, 300, 60);
        stepmode.setFont(font1);
        buttonGroup.add(stepmode);
        panel.add(stepmode);

        quickmode = new JRadioButton("QUICK MODE");
        quickmode.setBounds(75, 350, 300, 60);
        quickmode.setFont(font1);
        buttonGroup.add(quickmode);
        panel.add(quickmode);

        burstmode = new JRadioButton("BURST MODE");
        burstmode.setBounds(75, 400, 300, 60);
        burstmode.setFont(font1);
        burstmode.setSelected(true);
        buttonGroup.add(burstmode);
        panel.add(burstmode);

        execute = new JButton("EXECUTE");
        execute.setBounds(75, 500, 300, 40);
        execute.setFont(font1);
        execute.setEnabled(false);
        panel.add(execute);
        execute.addActionListener(this);

        nextline = new JButton("NEXT LINE");
        nextline.setBounds(75, 550, 300, 40);
        nextline.setFont(font1);
        nextline.setEnabled(false);
        panel.add(nextline);
        nextline.addActionListener(this);

        cancel = new JButton("CANCEL");
        cancel.setBounds(75, 600, 300, 40);
        cancel.setFont(font1);
        cancel.setEnabled(false);
        panel.add(cancel);
        cancel.addActionListener(this);

        int basex = 500, basey = 100;

        registerLabel = new JLabel("REGISTERS");
        registerLabel.setBounds(basex+120, basey, 150, 20);
        registerLabel.setFont(font1);
        registerLabel.setForeground(Color.white);
        panel.add(registerLabel);

        registers = new JLabel[16];
        regContents = new JTextField[16];

        for(int i=0;i<16;i++)
        {
            registers[i] = new JLabel(Integer.toString(i, 16).toUpperCase());
            registers[i].setBounds(basex+((i%4)*100), basey+50+((int)(i/4)*50), 40, 40);
            registers[i].setFont(font2);
            registers[i].setForeground(Color.white);
            panel.add(registers[i]);

            regContents[i] = new JTextField("0000");
            regContents[i].setBounds(basex+20+((i%4)*100), basey+50+((int)(i/4)*50), 40, 40);
            regContents[i].setFont(font2);
            panel.add(regContents[i]);
        }

        flags = new JLabel[5];
        flagContent = new JTextField[5];

        String flagname[] = {"ZERO","CARRY","SIGN","PARITY","OVERFLOW"};
        for(int i=0;i<5;i++)
        {
            flags[i] = new JLabel(flagname[i]);
            flags[i].setBounds(basex+(i*80), basey+270, 70, 30);
            flags[i].setFont(font2);
            flags[i].setForeground(Color.white);
            panel.add(flags[i]);

            flagContent[i] = new JTextField("0");
            flagContent[i].setBounds(basex+(i*80), basey+320, 45, 30);
            flagContent[i].setFont(font2);
            panel.add(flagContent[i]);
        }

        loc_counterLabel = new JLabel("Location Counter");
        loc_counterLabel.setBounds(500, 550, 200, 40);
        loc_counterLabel.setFont(font1);
        loc_counterLabel.setForeground(Color.white);
        panel.add(loc_counterLabel);

        location_counter = new JTextField("0000");
        location_counter.setBounds(700, 550, 100, 40);
        location_counter.setFont(font1);
        panel.add(location_counter);

        this.getContentPane().setLayout (null);
        this.getContentPane().add(panel);
        this.setSize(dimension);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public String normalize(String str)
    {
        while(str.length() < 4)
            str = "0" + str;
        return str.toUpperCase();
    }

    public int getBaseIndex(int count,int current_index)
    {
        String ESD_id = workspace[count][1],ESD_label = null;

        //System.out.println("For Current Index : "+current_index+" ESD_id at position "+count+" = "+ESD_id);

        for(int i=0; i<ESD_table[current_index].length && ESD_table[current_index][i][0]!=null; i++)
            if(normalize(ESD_table[current_index][i][3]).equals(ESD_id))
            {
                ESD_label = ESD_table[current_index][i][0];
                break;
            }
        //System.out.println("ESD label : "+ESD_label);

       for(int i=0; i<3; i++)
       {
           if(i==current_index)
               continue;
           //System.out.println("Length "+ESD_table[i].length+" "+ESD_table[i][0][0]);
           for(int j=0; j<ESD_table[i].length && ESD_table[i][j][0]!=null; j++)
           {
               //System.out.println("i = "+i+" j = "+j);
               if(ESD_table[i][j][0].equals(ESD_label) && (ESD_table[i][j][2].equals("SD") || ESD_table[i][j][2].equals("LD")))
               {
                   //System.out.println("FOUND");
                   ESD_index = j;
                   return i;
               }
           }
       }

        return -1;
    }

    public void load()
    {
        File file0 = new File("C:/Users/Vivek/Desktop/Assembler/ESD_file0.txt");
        File file1 = new File("C:/Users/Vivek/Desktop/Assembler/ESD_file1.txt");
        File file2 = new File("C:/Users/Vivek/Desktop/Assembler/ESD_file2.txt");

        String filepath0="C:/Users/Vivek/Desktop/Assembler/object_code_file0.txt";
        String filepath1="C:/Users/Vivek/Desktop/Assembler/object_code_file1.txt";
        String filepath2="C:/Users/Vivek/Desktop/Assembler/object_code_file2.txt";
        
        String header[] = new String[3];
        String record = null;
        
        Scanner sc0=null, sc1=null, sc2=null, sc=null;

        try
        {
            sc0 = new Scanner(new FileInputStream(file0));
            sc1 = new Scanner(new FileInputStream(file1));
            sc2 = new Scanner(new FileInputStream(file2));
            sc = null;

            for(int i=0,j=0,k=0;i<3;i++)
            {
                if(i == 0) sc = sc0;
                else if(i == 1) sc = sc1;
                else if(i == 2) sc = sc2;
                j=0;
                while(sc.hasNext())
                {
                    ESD_table[i][j][k] = sc.next();
                    k++;
                    if(k == 4)
                    {
                        k = 0;
                        j++;
                    }
                }
            }
        }
        catch(FileNotFoundException e)
        {
            workspace=null;
            JOptionPane.showMessageDialog(null, "File not found in ESD!", "Error Message", JOptionPane.ERROR_MESSAGE);
        }

        /*for(int i=0;i<3;i++)
        {
            for(int j=0;j<ESD_table[i].length;j++)
            {
                for(int k=0;k<4;k++)
                {
                    if(ESD_table[i][j][k] != null)
                        System.out.print(ESD_table[i][j][k]+" ");
                }
                System.out.println();
            }
            System.out.println();
        }*/
        /*JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
        chooser.setFileFilter(filter);
        chooser.setSelectedFile(new File(filepath));

        int returnVal = chooser.showOpenDialog(this);*/
        int line_no = 1, count = 0, current_index=-1, other_index=0;

        /*if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            filepath=chooser.getSelectedFile().getPath();*/

            file0 = new File(filepath0);
            file1 = new File(filepath1);
            file2 = new File(filepath2);
            
            workspace = new String [100][3];

            try
            {
                sc0 = new Scanner(new FileInputStream(file0));
                sc1 = new Scanner(new FileInputStream(file1));
                sc2 = new Scanner(new FileInputStream(file2));

                for(int i=0;i<3;i++)
                {
                    if(i == 0) sc = sc0;
                    else if(i == 1) sc = sc1;
                    else if(i == 2) sc = sc2;

                    header[i] = sc.next();
                    record = null;
                    if(header[i]==null || header[i].charAt(0) != 'H' || header[i].length() != 15)
                    {
                        workspace=null;
                        JOptionPane.showMessageDialog(null, "Error in header "+i+" record!", "Error Message", JOptionPane.ERROR_MESSAGE);
                    }

                    else
                    {
                        base_register[i] = count;
                        current_index++;
                        name = header[i].substring(1, 7);
                        initial_add = header[i].substring(7, 11);

                        int initial_add_int = Integer.parseInt(header[i].substring(7, 11), 16);
                        segment_length = Integer.parseInt(header[i].substring(11, 15), 16);
                        if(initial_add_int<0)
                        {
                            workspace=null;
                            JOptionPane.showMessageDialog(null, "Initial address invalid!", "Error Message", JOptionPane.ERROR_MESSAGE);
                        }
                        if(segment_length<0)
                        {
                            workspace=null;
                            JOptionPane.showMessageDialog(null, "Segment length invalid!", "Error Message", JOptionPane.ERROR_MESSAGE);
                        }
                        line_no++;
                        
                        record = sc.next();
                        while(record != null && (record.charAt(0) == 'T' || record.charAt(0) == 'D' || record.charAt(0) == 'X') && record.length() == 9)
                        {
                            workspace[count][0] = normalize(Integer.toHexString(count));
                            workspace[count][1] = record.substring(5, 9).toUpperCase();
                            Integer.parseInt(workspace[count][1],16);
                            workspace[count][2] = record.substring(0, 1);

                            if(workspace[count][2].equals("X"))
                            {
                                other_index = getBaseIndex(count,current_index);
                                workspace[count][1] = ESD_table[other_index][ESD_index][1];
                            }

                            record = sc.next();
                            line_no++;
                            count++;
                        }
                        if(record==null || record.charAt(0) != 'E' || record.length() != 5)
                        {
                            workspace=null;
                            JOptionPane.showMessageDialog(null, "Error at line number "+line_no, "Error Message", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            exec_add = record.substring(1, 5);
                            JOptionPane.showMessageDialog(null, "File loaded successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
            catch(FileNotFoundException e)
            {
                workspace=null;
                JOptionPane.showMessageDialog(null, "File not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
            }
            catch(NumberFormatException e)
            {
                workspace=null;
                JOptionPane.showMessageDialog(null, "Number format incorrect at line number "+line_no, "Error Message", JOptionPane.ERROR_MESSAGE);
            }
            catch(NoSuchElementException e)
            {
                workspace=null;
                JOptionPane.showMessageDialog(null, "Incomplete file!", "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        //}
        for(int i=0;i<3;i++)
            System.out.println("Base "+i+" = "+base_register[i]);
    }

    public int getIndex(int address)
    {
        String temp = normalize(Integer.toHexString(address));
        for(int i=0;i<workspace.length;i++)
        {
            if(workspace[i][0].equals(temp))
                return i;
        }
        return -1;
    }
    //Integer.parseInt(workspace[i][1], 16)

    public int getBase(char ch)
    {
        String str=""+ch;
        int ind=Integer.parseInt(str,16);

        if(ind>=0 && ind<=2)
            return base_register[ind];
        return -1;
    }

    public void jump(int flag, String condition)
    {
        line++;
        int temp = getBase(workspace[line][1].charAt(0)) + Integer.parseInt(workspace[line][1].substring(1), 16);
        if(temp > 65535)
        {
            flagContent[4].setText("1");
            JOptionPane.showMessageDialog(null, "Fatal Error! Address Overflow", "Error Message", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }

        if(flag == -1 || flagContent[flag].getText().equals(condition))
        {
            line = getIndex(temp) - 1;
        }
    }

    public boolean isEvenParity(int no)
    {
        String str = Integer.toBinaryString(no);
        int count=0;
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i) == '1')
                count++;
        }
        return (count%2 == 0);
    }

    public void setFlags(int no)
    {
        if(no == 0)
            flagContent[0].setText("1");
        else
            flagContent[0].setText("0");

        if(no > 65535)
        {
            flagContent[1].setText("1");
            no = no - 65536;
        }
        else
            flagContent[1].setText("0");

        if(no < 0)
            flagContent[2].setText("1");
        else
            flagContent[2].setText("0");

        if(isEvenParity(no))
            flagContent[3].setText("1");
        else
            flagContent[3].setText("0");
    }

    public void executeline()
    {
        String hexop = workspace[line][1];
        String opcode = hexop.substring(0,2);
        int op1 = Integer.parseInt(hexop.substring(2, 3),16);
        int op2 = Integer.parseInt(hexop.substring(3, 4),16);
        int temp=0;

        location_counter.setText(workspace[line][0]);
        
        switch(Integer.parseInt(opcode, 16))
        {
            case 0:
                temp = Integer.parseInt(regContents[op1].getText(),16) + Integer.parseInt(regContents[op2].getText(),16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 16:
                temp = Integer.parseInt(regContents[op1].getText(),16) + Integer.parseInt(regContents[op2].getText(),16) + Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 1:
                temp = Integer.parseInt(regContents[op1].getText(),16) + Integer.parseInt(workspace[++line][1],16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 17:
                temp = Integer.parseInt(regContents[op1].getText(),16) + Integer.parseInt(workspace[++line][1],16) + Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 2:
                line++;
                temp = Integer.parseInt(regContents[op1].getText(),16) +  Integer.parseInt(workspace[getIndex(getBase(workspace[line][1].charAt(0))+Integer.parseInt(workspace[line][1].substring(1) , 16))][1], 16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 18:
                line++;
                temp = Integer.parseInt(regContents[op1].getText(),16) + Integer.parseInt(workspace[getIndex(getBase(workspace[line][1].charAt(0))+Integer.parseInt(workspace[line][1].substring(1) , 16))][1], 16) + Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 32:
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(regContents[op2].getText(),16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 48:
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(regContents[op2].getText(),16) - Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 33:
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(workspace[++line][1],16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 49:
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(workspace[++line][1],16) - Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 34:
                line++;
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(workspace[getIndex(getBase(workspace[line][1].charAt(0))+Integer.parseInt(workspace[line][1].substring(1) , 16))][1], 16);
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 50:
                line++;
                temp = Integer.parseInt(regContents[op1].getText(),16) - Integer.parseInt(workspace[getIndex(getBase(workspace[line][1].charAt(0))+Integer.parseInt(workspace[line][1].substring(1) , 16))][1], 16) - Integer.parseInt(flagContent[1].getText());
                setFlags(temp);
                regContents[op1].setText(normalize(Integer.toHexString(temp)));
                break;

            case 64:
                regContents[op1].setText(regContents[op2].getText());
                break;

            case 65:
                regContents[op1].setText(workspace[++line][1]);
                break;

            case 66:
                if(op2 == 1)
                {
                    line++;
                    workspace[getIndex(getBase(workspace[line][1].charAt(0)) + Integer.parseInt(workspace[line][1].substring(1), 16))][1] = regContents[op1].getText();
                }
                else if(op2 == 0)
                {
                    line++;
                    regContents[op1].setText(workspace[getIndex(getBase(workspace[line][1].charAt(0)) + Integer.parseInt(workspace[line][1].substring(1), 16))][1]);
                }
                break;

            case 82:
                jump(-1,"-1");
                break;

            case 98:
                jump(0,"1");
                break;

            case 114:
                jump(0,"0");
                break;

            case 130:
                jump(1,"1");
                break;

            case 146:
                jump(1,"0");
                break;

            case 162:
                jump(2,"1");
                break;

            case 178:
                jump(2,"0");
                break;

            case 194:
                jump(3,"1");
                break;

            case 210:
                jump(3,"0");
                break;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == loadfile)
        {
            load();
            execute.setEnabled(true);
        }
        else if(e.getSource() == displaycontent)
        {
            if(workspace!=null)
                new Display(workspace);
            else
                JOptionPane.showMessageDialog(null, "No file loaded!", "Error Message", JOptionPane.ERROR_MESSAGE);
        }
        else if(e.getSource() == execute)
        {
            for(int i=0;i<16;i++)
                regContents[i].setText("0000");
            for(int i=0;i<5;i++)
                flagContent[i].setText("0");
            
            if(stepmode.isSelected())
            {
                line = 0;
                nextline.setEnabled(true);
                execute.setEnabled(false);
                cancel.setEnabled(true);
                executeline();
                line++;
            }
            if(quickmode.isSelected())
            {
                JOptionPane.showMessageDialog(null, "Execution Complete!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            if(burstmode.isSelected())
            {
                line = 0;
                while(workspace[line][2].equals("T"))
                {
                    executeline();
                    line++;
                }
                JOptionPane.showMessageDialog(null, "Execution Complete!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if(e.getSource() == nextline)
        {
            if(line < workspace.length && workspace[line][2].equals("T"))
            {
                executeline();
                line++;
            }
            else
            {
                nextline.setEnabled(false);
                cancel.setEnabled(false);
                execute.setEnabled(true);
                JOptionPane.showMessageDialog(null, "Execution Complete!", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if(e.getSource() == cancel)
        {
            line = 0;
            for(int i=0;i<16;i++)
                regContents[i].setText("0000");
            for(int i=0;i<5;i++)
                flagContent[i].setText("0");
            execute.setEnabled(true);
            nextline.setEnabled(false);
            cancel.setEnabled(false);
        }
    }

    public static void main(String[] args)
    {
        new Simulator();
    }
}
