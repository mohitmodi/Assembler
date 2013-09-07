package assembler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Assembler extends JFrame implements ActionListener
{
    JPanel panel = null;
    JTextArea source_code = new JTextArea();
    JTextArea im_code = new JTextArea();
    JTextArea object_code = new JTextArea();
    JButton load_source,save_source,save_as,assemble,display_sym,display_lit;
    JLabel source_code_label = new JLabel("SOURCE CODE");
    JLabel im_code_label = new JLabel("INTERMEDIATE CODE");
    JLabel object_code_label = new JLabel("OBJECT CODE");
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

    String inputfile = null;
   String symbolfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/symbol_table.txt";
    String literalfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/literal_table.txt";
    String motfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/machine_op_table.txt";
    String potfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/pseudo_op_table.txt";
    String imfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/intermediate_file.txt";
    String object_codefile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/object_code_file";
    String ESDfile = "C:/Users/mohit/Desktop/final_assembler/Files/Assembler_files/ESD_file";
    String segment_name = null;
    String symbol_table[][] = new String[100][4];
    String literal_table[][] = new String[50][4];
    String ESD_table[][] = new String[50][4];
    String mot[][] = null, pot[] = null;
    int symbol_count=0,literal_count=0,ESD_count=0,ext_count=0;
    String base = null;
    final String START = "START", EQ = "EQUAL", DC = "DEFCT", END = "SGEND";

    class InputFileFormatException extends Exception
    {
        public String message;
        int line_no;
        public InputFileFormatException(String msg,int line)
        {
            message=msg;
            line_no=line;
        }
    }

    class IntermediateFileFormatException extends Exception
    {
        public String message;
        int line_no;
        public IntermediateFileFormatException(String msg,int line)
        {
            message=msg;
            line_no=line;
        }
    }

    public Assembler()
    {
        super("Assembler");

        panel = new JPanel()
        {
            @Override
            public void paintComponent(Graphics g)
            {
                g.drawImage(new ImageIcon("C:/Users/Vivek/Desktop/Assembler/assemblerbackground.jpg").getImage(), 0, 0, null);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(dimension);
        panel.setBounds(0,0,dimension.width,dimension.height);
        panel.revalidate();

        Font font1 = new Font("Arial", Font.BOLD, 20);
        Font font2 = new Font("Serif", Font.PLAIN, 20);

        source_code_label.setBounds(150, 20, 200, 20);
        source_code_label.setFont(font1);
        source_code_label.setForeground(Color.white);
        panel.add(source_code_label);

        source_code.setFont(font2);
        JScrollPane scroll_source=new JScrollPane(source_code);
        scroll_source.setBounds(50, 50, 350, 550);
        panel.add(scroll_source);

        im_code_label.setBounds(500, 20, 300, 20);
        im_code_label.setFont(font1);
        im_code_label.setForeground(Color.white);
        panel.add(im_code_label);

        im_code.setFont(font2);
        im_code.setEditable(false);
        JScrollPane scroll_im=new JScrollPane(im_code);
        scroll_im.setBounds(450, 50, 300, 550);
        panel.add(scroll_im);

        object_code_label.setBounds(850, 20, 200, 20);
        object_code_label.setFont(font1);
        object_code_label.setForeground(Color.white);
        panel.add(object_code_label);

        object_code.setFont(font2);
        object_code.setEditable(false);
        JScrollPane scroll_obj=new JScrollPane(object_code);
        scroll_obj.setBounds(800, 50, 250, 550);
        panel.add(scroll_obj);

        load_source = new JButton("LOAD");
        load_source.setBounds(100,620,200,40);
        load_source.setFont(font1);
        panel.add(load_source);
        load_source.addActionListener(this);

        save_source = new JButton("SAVE");
        save_source.setBounds(400,620,200,40);
        save_source.setFont(font1);
        save_source.setEnabled(false);
        panel.add(save_source);
        save_source.addActionListener(this);

        save_as = new JButton("SAVE AS");
        save_as.setBounds(700,620,200,40);
        save_as.setFont(font1);
        panel.add(save_as);
        save_as.addActionListener(this);

        assemble = new JButton("ASSEMBLE");
        assemble.setBounds(1100,100,200,40);
        assemble.setFont(font1);
        panel.add(assemble);
        assemble.addActionListener(this);

        display_sym = new JButton("SYMBOL TABLE");
        display_sym.setBounds(1100,200,200,40);
        display_sym.setFont(font1);
        panel.add(display_sym);
        display_sym.addActionListener(this);

        display_lit = new JButton("LITERAL TABLE");
        display_lit.setBounds(1100,300,200,40);
        display_lit.setFont(font1);
        panel.add(display_lit);
        display_lit.addActionListener(this);

        this.getContentPane().setLayout(null);
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
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == load_source)
        {
            String filepath="C:/Users/Vivek/Desktop/Assembler/sample.txt";
            JFileChooser chooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(filepath));

            int returnVal = chooser.showOpenDialog(this);

            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    inputfile = chooser.getSelectedFile().getPath();
                    Scanner sc = new Scanner(new FileInputStream(new File(inputfile)));
                    String source_str = "";
                    while(sc.hasNextLine())
                        source_str += sc.nextLine()+"\n";
                    source_code.setText(source_str);

                    save_source.setEnabled(true);
                }
                catch(FileNotFoundException ex)
                {
                    JOptionPane.showMessageDialog(null, "File not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        else if(e.getSource() == save_source)
        {
            writeToFile(inputfile,source_code.getText());
            JOptionPane.showMessageDialog(null, "File saved successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
        }

        else if(e.getSource() == save_as)
        {
            String filepath="C:/Users/Vivek/Desktop/Assembler/sample2.txt";
            JFileChooser chooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(filepath));
            
            int returnVal = chooser.showSaveDialog(this);
            int option = 0;

            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                inputfile = chooser.getSelectedFile().getPath();
                if((new File(inputfile)).exists())
                    option = JOptionPane.showConfirmDialog(null, "The specified file already exists. Overwrite?","Confirmation",JOptionPane.OK_CANCEL_OPTION);

                if(option == 0)
                {
                    writeToFile(inputfile,source_code.getText());
                    JOptionPane.showMessageDialog(null, "File saved successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        else if(e.getSource()==assemble)
        {
            im_code.setText("");
            object_code.setText("");

            try
            {
                Scanner sc = new Scanner(new FileInputStream(new File(motfile)));
                int no_instruction = Integer.parseInt(sc.next());
                mot = new String[no_instruction][3];
                for(int i=0;i<no_instruction;i++)
                {
                    mot[i][0] = sc.next();
                    mot[i][1] = sc.next();
                    mot[i][2] = sc.next();
                }

                sc = new Scanner(new FileInputStream(new File(potfile)));
                no_instruction = Integer.parseInt(sc.next());
                pot = new String[no_instruction];
                for(int i=0;i<no_instruction;i++)
                    pot[i] = sc.next();

                try
                {
                    pass1();
                    sc = new Scanner(new FileInputStream(new File(imfile)));
                    String str = "";
                    while(sc.hasNextLine())
                        str += sc.nextLine()+"\n";
                    im_code.setText(str);

                    pass2();
                    sc = new Scanner(new FileInputStream(new File(object_codefile+base+".txt")));
                    str = "";
                    while(sc.hasNextLine())
                        str += sc.nextLine()+"\n";
                    object_code.setText(str);

                    JOptionPane.showMessageDialog(null, "Code Assembled successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                }
                catch(InputFileFormatException ex)
                {
                    JOptionPane.showMessageDialog(null, ex.message+"\nLine number "+ex.line_no, "Input Code Error", JOptionPane.ERROR_MESSAGE);
                }
                catch(IntermediateFileFormatException ex)
                {
                    JOptionPane.showMessageDialog(null, ex.message+"\nLine number "+ex.line_no, "Intermediate Code Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(FileNotFoundException ex)
            {
                JOptionPane.showMessageDialog(null, "Opcode File not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        }

        else if(e.getSource()==display_sym)
        {
            new DisplaySymbolTable(symbolfile,symbol_count);
        }

        else if(e.getSource()==display_lit)
        {
            new DisplayLiteralTable(literalfile,literal_count);
        }
    }

    public int motSearch(String str)
    {
        int length = mot.length;
        for(int i=0;i<length;i++)
            if(mot[i][0].equals(str))
                return i;
        
        return -1;
    }

    public int potSearch(String str)
    {
        int length = pot.length;
        for(int i=0;i<length;i++)
            if(pot[i].equals(str))
                return i;

        return -1;
    }

    public boolean isHexString(String str)
    {
        if(str.charAt(0) != 'x')
            return false;
        try
        {
            str = str.substring(1);
            if(Integer.parseInt(str,16) > 65535 || Integer.parseInt(str,16) < 0)
                return false;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public boolean isDecString(String str)
    {
        if(str.charAt(0) != '#')
            return false;
        try
        {
            str = str.substring(1);
            if(Integer.parseInt(str) > 65535 || Integer.parseInt(str) < 0)
                return false;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public boolean isSymbol(String str)
    {
        return (str.length()==6 && Character.isLetter(str.charAt(0)) && str.charAt(0)!='x' && str.charAt(0)!='R');
    }

    public boolean isLiteral(String str)
    {
        return (str.length() >= 6 && str.charAt(0) == '=' && ( isHexString(str.substring(1)) || isDecString(str.substring(1)) ) );
    }

    public int symtableSearch(String str)
    {
        for(int i=0;i<symbol_count;i++)
            if(symbol_table[i][0].equals(str))
                return i;
        
        return -1;
    }

    public int littableSearch(String str)
    {
        for(int i=0;i<literal_count;i++)
            if(literal_table[i][0].equals(str))
                return i;

        return -1;
    }

    public int ESDSearch(String str)
    {
        for(int i=0;i<ESD_count;i++)
            if(ESD_table[i][0].equals(str))
                return i;

        return -1;
    }

    public void writeToFile(String filename, String filestr)
    {
        byte buffer[]=null;
        try
        {
            OutputStream fout = new FileOutputStream(filename);

            buffer = filestr.getBytes();
            try
            {
                for(int i=0;i<buffer.length;i++)
                   fout.write(buffer[i]);
                fout.close();
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "File '"+filename+"' not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void pass1() throws InputFileFormatException
    {
        int loc_counter=0,mot_index=0,pot_index=0,line_no=1,temp=0;
        String label = null, opcode = null, opmod = null, op1 = null, op2 = null;
        boolean data_segment = false;

        symbol_table = new String[100][4];
        literal_table = new String[50][4];
        ESD_table = new String[50][4];
        symbol_count = literal_count = ESD_count = ext_count = 0;
        base="0";
        
        try
        {
            //Scanner sc = new Scanner(new FileInputStream(new File(inputfile)));
            Scanner sc = new Scanner(source_code.getText());

            segment_name = sc.next();
            if(segment_name.length() != 6 || !Character.isLetter(segment_name.charAt(0)))
                throw new InputFileFormatException("Segment Name error!",line_no);

            opcode = sc.next();
            if(!opcode.equals(START))
                throw new InputFileFormatException("Start error!",line_no);
            
            String filestr_im = "";

            sc.nextLine();
            label = sc.next();

            ESD_table[ESD_count][0] = segment_name;
            ESD_table[ESD_count][1] = "0";
            ESD_table[ESD_count][2] = "SD";
            ESD_table[ESD_count][3] = Integer.toHexString(ext_count++);
            ESD_count++;

            while(true)
            {
                line_no++;
                if(label.charAt(0) == ';')
                {
                    sc.nextLine();
                    label = sc.next();
                    continue;
                }

                //  ENTRY INTO SYMBOL TABLE
                opcode = sc.next();

                if(!label.equals("______"))
                {
                    if(!isSymbol(label))
                        throw new InputFileFormatException("Invalid Symbol Name!",line_no);

                    if(symtableSearch(label) == -1)
                    {
                        if(opcode.equals("ENTRY"))
                        {
                            ESD_table[ESD_count][0] = label;
                            ESD_table[ESD_count][1] = "0";
                            ESD_table[ESD_count][2] = "LD";
                            ESD_table[ESD_count][3] = "-";
                            ESD_count++;
                        }
                        else if(opcode.equals("EXTRN"))
                        {
                            ESD_table[ESD_count][0] = label;
                            ESD_table[ESD_count][1] = "0";
                            ESD_table[ESD_count][2] = "ER";
                            ESD_table[ESD_count][3] = Integer.toHexString(ext_count++);
                            ESD_count++;
                        }
                        else
                            symbol_table[symbol_count][0] = label;
                    }
                    else
                        throw new InputFileFormatException("Duplicate Symbol Error!",line_no);

                    if(opcode.equals(EQ))
                    {
                        op1 = sc.next();
                        if(isHexString(op1))
                            symbol_table[symbol_count][1] = op1.substring(1);
                        else if(isDecString(op1))
                            symbol_table[symbol_count][1] = Integer.toHexString(Integer.parseInt(op1.substring(1))).toUpperCase();
                        else
                            throw new InputFileFormatException("Operand error!",line_no);

                        symbol_table[symbol_count][2] = "0";
                        symbol_table[symbol_count][3] = "A";
                        sc.nextLine();
                        label = sc.next();
                        symbol_count++;
                        continue;
                    }
                    else if(opcode.equals("ENTRY") || opcode.equals("EXTRN"))
                    {}
                    else
                    {
                        symbol_table[symbol_count][1] = Integer.toString(loc_counter,16).toUpperCase();
                        symbol_table[symbol_count][2] = "2";
                        symbol_table[symbol_count][3] = "R";
                        symbol_count++;
                        if(opcode.equals(DC))
                        {
                            data_segment = true;
                            filestr_im += Integer.toString(loc_counter,16).toUpperCase()+" DEFCT ";

                            op1 = sc.next();
                            if (isHexString(op1))
                                filestr_im += op1.substring(1)+"\n";
                            else if(isDecString(op1))
                                filestr_im += Integer.toHexString(Integer.parseInt(op1.substring(1))).toUpperCase()+"\n";
                            else
                                throw new InputFileFormatException("Operand error!",line_no);

                            loc_counter += 1;
                            sc.nextLine();
                            label = sc.next();
                            continue;
                        }
                    }
                }

                //  SKIP MOT AND POT ENTRY
                if(opcode.length() != 5)
                    throw new InputFileFormatException("Operation error!",line_no);

                if(opcode.equals(END))
                    break;

                mot_index = motSearch(opcode);
                if(mot_index == -1)
                {
                    pot_index = potSearch(opcode);
                    if(pot_index == -1)
                        throw new InputFileFormatException("Opcode not found!",line_no);
                       
                    else
                    {
                        if(opcode.equals("SBASE"))
                        {
                            op1 = sc.next();
                            if(isHexString(op1) && op1.length()==2)
                                base = op1.substring(1);
                            else if(isDecString(op1) && (op1.length()==2 || op1.length()==3))
                                base = Integer.toHexString(Integer.parseInt(op1.substring(1)));
                            else
                                throw new InputFileFormatException("Operand error!",line_no);
                        }
                        sc.nextLine();
                        label = sc.next();
                        continue;
                    }
                }
                else if(data_segment == false)
                    opmod = mot[mot_index][2];
                else
                    throw new InputFileFormatException("Machine Opcode encountered in data segment!",line_no);

                filestr_im += Integer.toString(loc_counter,16).toUpperCase()+" "+opcode+" ";

                op1=op2="";
                if(opmod.equals("34"))
                {
                    loc_counter += 1;
                    op1 = sc.next();
                    op2 = sc.next();
                }
                else if(opmod.equals("3N"))
                {
                    loc_counter += 2;
                    op1 = sc.next();
                    op2 = sc.next();
                }
                else if(opmod.equals("-N"))
                {
                    loc_counter += 2;
                    op1 = sc.next();
                }

                //  ENTRY INTO LITERAL TABLE

                if(!op1.equals(""))
                {
                    if(isLiteral(op1))
                    {
                        if(littableSearch(op1)==-1)
                        {
                            literal_table[literal_count][0] = op1;
                            literal_table[literal_count][1] = null;
                            literal_table[literal_count][2] = "2";
                            literal_table[literal_count][3] = "R";
                            literal_count++;
                        }
                    }
                    else if(isHexString(op1))
                        op1 = op1.substring(1);
                    else if(isDecString(op1))
                        op1 = Integer.toHexString(Integer.parseInt(op1.substring(1))).toUpperCase();
                    else if(!isSymbol(op1))
                        throw new InputFileFormatException("Operand error!",line_no);
                }
                if(!op2.equals(""))
                {
                    if(isLiteral(op2))
                    {
                        if(littableSearch(op2)==-1)
                        {
                            literal_table[literal_count][0] = op2;
                            literal_table[literal_count][1] = null;
                            literal_table[literal_count][2] = "2";
                            literal_table[literal_count][3] = "R";
                            literal_count++;
                        }
                    }
                    else if(isHexString(op2))
                        op2 = op2.substring(1);
                    else if(isDecString(op2))
                        op2 = Integer.toHexString(Integer.parseInt(op2.substring(1))).toUpperCase();
                    else if(!isSymbol(op2))
                        throw new InputFileFormatException("Operand error!",line_no);
                }

                filestr_im += op1+" "+op2+"\n";

                sc.nextLine();
                label = sc.next();
            }

            for(int i=0; i<literal_count; i++)
            {
                literal_table[i][1] = Integer.toString(loc_counter,16).toUpperCase();
                loc_counter += 1;
            }

            for(int i=0; i<ESD_count; i++)
            {
                if(ESD_table[i][2].equals("LD"))
                {
                    temp = symtableSearch(ESD_table[i][0]);
                    if(temp == -1)
                        throw new InputFileFormatException("Symbol not found",line_no);
                    else
                    {
                        if(symbol_table[temp][3].equals("A"))
                            ESD_table[i][1] = normalize(symbol_table[temp][1]);
                        else
                            ESD_table[i][1] = base + normalize(symbol_table[temp][1]).substring(1);
                    }
                }
                else if(ESD_table[i][2].equals("SD"))
                    ESD_table[i][1] = base+"000";
            }

            filestr_im = segment_name+" "+Integer.toString(loc_counter,16).toUpperCase() + "\n" + filestr_im;
            writeToFile(imfile,filestr_im);

            String filestr_sym = "";
            for(int i=0;i<symbol_count;i++)
                filestr_sym += symbol_table[i][0]+" "+symbol_table[i][1]+" "+symbol_table[i][2]+" "+symbol_table[i][3]+"\n";
            writeToFile(symbolfile,filestr_sym);
            
            String filestr_lit = "";
            for(int i=0;i<literal_count;i++)
                filestr_lit += literal_table[i][0]+" "+literal_table[i][1]+" "+literal_table[i][2]+" "+literal_table[i][3]+"\n";
            writeToFile(literalfile,filestr_lit);

            String filestr_ESD = "";
            for(int i=0;i<ESD_count;i++)
                filestr_ESD += ESD_table[i][0]+" "+ESD_table[i][1]+" "+ESD_table[i][2]+" "+ESD_table[i][3]+"\n";
            writeToFile(ESDfile+base+".txt",filestr_ESD);
        }
        catch(NoSuchElementException e)
        {
            throw new InputFileFormatException("Input File Incomplete!",line_no);
        }
    }

    public void pass2() throws IntermediateFileFormatException
    {
        String startexe_address = "0000", opcode=null, opmod=null, op1 = null, op2 = null, temp = null;
        char ch;
        int address = 0, opcode_index=0, sym_index=0, lit_index=0, ESD_index=0, line_no=1;
        
        String filestr_objfile = "H";

        try
        {
            Scanner sc = new Scanner(new FileInputStream(new File(imfile)));
            filestr_objfile += sc.next()+startexe_address;

            temp = normalize(sc.next());
            filestr_objfile += temp+"\n";

            while(true)
            {
                line_no++;
                if(!sc.hasNext())
                    break;

                sc.next();
                opcode_index = motSearch(sc.next());
                if(opcode_index == -1)
                {
                    filestr_objfile += "D";
                    filestr_objfile += normalize(Integer.toString(address++,16));
                    
                    filestr_objfile += normalize(sc.next())+"\n";
                    continue;
                }

                filestr_objfile += "T" + normalize(Integer.toString(address++,16));

                opcode = mot[opcode_index][1];
                opmod = mot[opcode_index][2];

                if(opmod.equals("34"))
                {
                    op1 = sc.next();
                    op2 = sc.next();
                    if(op1.length() != 1)
                    {
                        if(!isSymbol(op1))
                            throw new IntermediateFileFormatException("Operand error!",line_no);

                        sym_index = symtableSearch(op1);
                        if(sym_index == -1)
                        {
                            if(ESDSearch(op1) == -1)
                                throw new IntermediateFileFormatException("Symbol not found!",line_no);
                            else
                                op1 = "0";
                        }
                        else
                        {
                            op1 = symbol_table[sym_index][1];
                            if(symbol_table[sym_index][3].equals("R"))
                                throw new IntermediateFileFormatException("Relocatable Symbol Error!",line_no);

                            if(op1.length() != 1)
                                throw new IntermediateFileFormatException("Operand error!",line_no);
                        }
                    }
                    if(op2.length() != 1)
                    {
                        if(!isSymbol(op2))
                            throw new IntermediateFileFormatException("Operand error!",line_no);

                        sym_index = symtableSearch(op2);
                        if(sym_index == -1)
                        {
                            if(ESDSearch(op2) == -1)
                                throw new IntermediateFileFormatException("Symbol not found!",line_no);
                            else
                                op2 = "0";
                        }
                        else
                        {
                            op2 = symbol_table[sym_index][1];
                            if(symbol_table[sym_index][3].equals("R"))
                                throw new IntermediateFileFormatException("Relocatable symbol error!",line_no);

                            if(op2.length() != 1)
                                throw new IntermediateFileFormatException("Operand error!",line_no);
                        }
                    }

                    opcode = opcode.substring(0, 2);
                    opcode += op1+op2;
                    filestr_objfile += opcode+"\n";
                }
                else if(opmod.equals("3N"))
                {
                    op1 = sc.next();
                    op2 = sc.next();
                    ESD_index = -1;

                    if(op1.length() != 1)
                    {
                        if(!isSymbol(op1))
                            throw new IntermediateFileFormatException("Operand error!",line_no);

                        sym_index = symtableSearch(op1);
                        if(sym_index == -1)
                        {
                            if(ESDSearch(op1) == -1)
                                throw new IntermediateFileFormatException("Symbol not found!",line_no);
                            else
                                op1 = "0";
                        }
                        else
                        {
                            op1 = symbol_table[sym_index][1];
                            if(symbol_table[sym_index][3].equals("R"))
                                throw new IntermediateFileFormatException("Relocatable Symbol Error!",line_no);

                            if(op1.length() != 1)
                                throw new IntermediateFileFormatException("Operand error!",line_no);
                        }
                    }
                    if(isSymbol(op2))
                    {
                        sym_index = symtableSearch(op2);
                        if(sym_index == -1)
                        {
                            ESD_index = ESDSearch(op2);
                            if(ESD_index == -1)
                                throw new IntermediateFileFormatException("Symbol not found!",line_no);
                            else
                                op2 = normalize(ESD_table[ESD_index][3]);
                        }
                        else
                        {
                            op2 = normalize(symbol_table[sym_index][1]);
                            if(opcode.charAt(1) == '2')
                               op2 = base + op2.substring(1);
                        }
                    }
                    else if(isLiteral(op2))
                    {
                        lit_index = littableSearch(op2);
                        if(lit_index == -1)
                            throw new IntermediateFileFormatException("Literal not found!",line_no);

                        op2 = normalize(literal_table[lit_index][1]);
                        if(opcode.charAt(1) == '2')
                            op2 = base + op2.substring(1);
                    }
                    else
                        throw new IntermediateFileFormatException("Operand is neither a symbol nor a literal!",line_no);

                    ch = opcode.charAt(3);
                    opcode = opcode.substring(0, 2);
                    opcode += op1+ch;
                    filestr_objfile += opcode;
                    
                    if(ESD_index == -1)
                        filestr_objfile += "\nT";
                    else
                        filestr_objfile += "\nX";

                    filestr_objfile += normalize(Integer.toString(address++,16))+op2+"\n";
                }
                else if(opmod.equals("-N"))
                {
                    op1 = sc.next();
                    ESD_index = -1;

                    if(isSymbol(op1))
                    {
                        sym_index = symtableSearch(op1);
                        if(sym_index == -1)
                        {
                            ESD_index = ESDSearch(op1);
                            if(ESD_index == -1)
                                throw new IntermediateFileFormatException("Symbol not found!",line_no);
                            else
                                op1 = normalize(ESD_table[ESD_index][3]);
                        }
                        else
                        {
                            op1 = normalize(symbol_table[sym_index][1]);
                            op1 = base + op1.substring(1);
                        }
                    }
                    else
                        throw new IntermediateFileFormatException("Operand is not a symbol!",line_no);

                    filestr_objfile += opcode;

                    if(ESD_index == -1)
                        filestr_objfile += "\nT";
                    else
                        filestr_objfile += "\nX";

                    filestr_objfile += normalize(Integer.toString(address++,16))+op1+"\n";
                }
            }

            for(int i=0;i<literal_count;i++)
                filestr_objfile += "D"+normalize(Integer.toString(address++,16))+ (literal_table[i][0].charAt(1) == 'x' ? literal_table[i][0].substring(2) : Integer.toHexString(Integer.parseInt(literal_table[i][0].substring(2)))) +"\n";

            filestr_objfile += "E"+startexe_address;

            writeToFile(object_codefile+base+".txt",filestr_objfile);
        }
        catch(FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "Intermediate File not found!", "Error Message", JOptionPane.ERROR_MESSAGE);
        }
        catch(NoSuchElementException e)
        {
            throw new IntermediateFileFormatException("Intermediate File Incomplete!",line_no);
        }
    }

    public static void main(String[] args)
    {
        new Assembler();
    }
}
