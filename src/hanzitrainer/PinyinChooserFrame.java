package hanzitrainer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JComponent;
import java.util.*;
import javax.swing.text.*;

/**
 *
 * @author Administrator
 */
public class PinyinChooserFrame extends JPanel
        implements javax.swing.event.DocumentListener
{

    private ArrayList<javax.swing.JComponent> entry_list;
    private int number_of_boxes;
    private FlowLayout panel_layout;
    JScrollPane scroller_container;
    int entry_length;
    private javax.swing.JTextArea to_update = null;

    public PinyinChooserFrame(JScrollPane scroller, HanziDB db, javax.swing.JTextArea log)
    {
        super();
        scroller_container = scroller;
        entry_list = new ArrayList<javax.swing.JComponent>();
        number_of_boxes = 0;
        panel_layout = new FlowLayout(FlowLayout.LEFT);
        setLayout(panel_layout);
        to_update = log;
        entry_length = 0;
    }

    public PinyinChooserFrame(JScrollPane scroller, HanziDB db)
    {
        super();
        scroller_container = scroller;
        entry_list = new ArrayList<javax.swing.JComponent>();
        number_of_boxes = 0;
        panel_layout = new FlowLayout(FlowLayout.LEFT);
        setLayout(panel_layout);
        entry_length = 0;
    }

    private void debuglog(String log)
    {
        if (to_update != null)
        {
            to_update.append(log);
        }
    }

    private Boolean is_chinese_char(String input)
    {
        int entry;
        if (input.codePointCount(0, input.length()) != 1)
        {
            return false;
        }
        else
        {
            entry = input.codePointAt(0);
            if ((entry >= 0x4E00) && (entry <= 0x9fff))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private void add_item(int index, String entry)
    {
        if (is_chinese_char(entry))
        {
            add_combo_box(index, new String[]
                    {
                        entry, "a", "b"
                    });
        }
        else
        {
            add_label(index, entry);
        }
    }

    private void add_combo_box(int index, String[] content)
    {
        JComboBox temp_box = new JComboBox();
        temp_box.setModel(new javax.swing.DefaultComboBoxModel(content));
        entry_list.add(index, temp_box);
        number_of_boxes++;
        update_layout();
    }

    private void add_label(int index, String content)
    {
        JLabel temp_label = new JLabel();
        temp_label.setText(content);
        entry_list.add(index, temp_label);
        number_of_boxes++;
        update_layout();
    }

    private void remove_combo_box(int index)
    {
        JComponent item = (JComponent) entry_list.get(index);
        entry_list.remove(index);
        number_of_boxes--;
        update_layout();
        panel_layout.removeLayoutComponent(item);
    }

    public ArrayList get_pinyins()
    {
        ArrayList<String> res = new ArrayList<String>();
        int i;

        for (i = 0; i < number_of_boxes; i++)
        {
            if (entry_list.get(i) instanceof JLabel)
            {
                continue;
            }
            else
            {
                res.add(entry_list.get(i).toString().toLowerCase());
            }
        }
        return res;
    }

    private void update_layout()
    {
        int i;

        removeAll();

        for (i = 0; i < number_of_boxes; i++)
        {
            add((Component) entry_list.get(i));
        }

        scroller_container.setViewportView(this);
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e)
    {
        // not sure there is anything to do here...
    }

    public void insertUpdate(javax.swing.event.DocumentEvent e)
    {
        Document doc = e.getDocument();
        String inserted;
        int i;

        e.getChange(e.getDocument().getDefaultRootElement());
        try
        {
            inserted = doc.getText(e.getOffset(), e.getLength());
            if ((Character.isWhitespace(inserted.codePointAt(e.getLength() - 1))))
            {
                debuglog("not yet\n");
            }
            else
            {
                if (e.getOffset() > entry_length)
                {
                    debuglog("inserted " + (e.getOffset() - entry_length) + " spaces\n");
                    for (i = 0; i < e.getOffset() - entry_length; i++)
                    {
                        add_label(e.getOffset(), new String(" "));
                    }
                }
                for (i = e.getLength() - 1; i >= 0; i--)
                {
                    add_item(e.getOffset(), doc.getText(e.getOffset() + i, 1));
                }
                debuglog("inserted " + e.getLength() + " long, at " + e.getOffset() + " added [" + doc.getText(e.getOffset(), e.getLength()) + "]\n");
                entry_length = doc.getLength();
            }

        }
        catch (BadLocationException ex)
        {
            debuglog("bad location !\n");
            Logger.getLogger(PinyinChooserFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e)
    {
        Document doc = e.getDocument();
        int i;

        if (doc.getLength() == entry_length)
        {
            debuglog("no change\n");
        }
        else
        {
            debuglog(doc.getLength() + " != " + entry_length + "\n");
            entry_length = doc.getLength();
            try
            {
                debuglog("removed " + e.getLength() + " long, at " + e.getOffset() + " now [" + doc.getText(0, doc.getLength()) + "]\n");
                for (i = 0; i < e.getLength(); i++)
                {
                    remove_combo_box(e.getOffset());
                }
            }
            catch (BadLocationException ex)
            {
                debuglog("bad location !\n");
                Logger.getLogger(PinyinChooserFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
