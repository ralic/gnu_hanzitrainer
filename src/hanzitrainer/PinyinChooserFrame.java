/*
 * 
 * HanziTrainer to help you learn Mandarin
 * Copyright (C) 2008  Matthieu Jeanson
 *
 * Copyright (c) 2008, Matthieu Jeanson ( matthieu.jeanson[at]gmail.com )
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name its contributors may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY MATTHIEU JEANSON ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MATTHIEU JEANSON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package hanzitrainer;

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
    private JScrollPane scroller_container;
    private int entry_length;
    private javax.swing.JTextArea to_update = null;
    private HanziDB database;

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
        database = db;
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
        database = db;
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
            ArrayList<String> possibilities = database.get_pinyin_from_character(entry);
            add_combo_box(index, possibilities);
        }
        else
        {
            add_label(index, entry);
        }
    }

    private void add_combo_box(int index, ArrayList<String> content)
    {
        int i;
        JComboBox temp_box = new JComboBox();
        //temp_box.setModel(new javax.swing.DefaultComboBoxModel(content));
        for (i=0; i<content.size(); i++)
        {
            temp_box.addItem(content.get(i));
        }
        temp_box.setEditable(true);
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

    public ArrayList<String> get_pinyins()
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
                JComboBox box = (JComboBox)(entry_list.get(i));
                String selected = (String)(box.getSelectedItem());
                res.add(selected);
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
