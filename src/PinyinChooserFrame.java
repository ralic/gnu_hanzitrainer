/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.util.*;

public class PinyinChooserFrame extends JPanel
        implements javax.swing.event.DocumentListener {

    private ArrayList combo_box_list;
    private int number_of_boxes;
    private GroupLayout panel_layout;
    JScrollPane scroll_container;

    public PinyinChooserFrame(JScrollPane scroller) {
        super();
        combo_box_list = new ArrayList<JComboBox>();
        number_of_boxes = 0;
        panel_layout = new GroupLayout(this);
        this.setLayout(panel_layout);
        this.scroll_container = scroller;
        scroller.setViewportView(this);
    }
    
    private void add_combo_box(int index,String[] content) {
        JComboBox temp_box = new JComboBox();
        temp_box.setModel(new javax.swing.DefaultComboBoxModel(content));
        this.combo_box_list.add(index, temp_box);
        number_of_boxes++;
        this.update_layout();
    }
    
    private void remove_combo_box(int index) {
        this.combo_box_list.remove(index);
        number_of_boxes--;
        this.update_layout();
    }
    
    private void update_layout() {
        int i;
        GroupLayout.ParallelGroup temp_par_group;
        GroupLayout.SequentialGroup temp_seq_group;

        temp_seq_group = panel_layout.createSequentialGroup().addContainerGap();

        for (i = 0; i < number_of_boxes; i++) {
            temp_seq_group = temp_seq_group.addComponent((Component) this.combo_box_list.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
            if (i<number_of_boxes-1)
                temp_seq_group = temp_seq_group.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        }
        temp_seq_group = temp_seq_group.addContainerGap();

        panel_layout.setHorizontalGroup(panel_layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(temp_seq_group));

        temp_par_group = panel_layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE);
        for (i = 0; i < number_of_boxes; i++) {
            temp_par_group = temp_par_group.addComponent((Component) this.combo_box_list.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
        }   
        
        panel_layout.setVerticalGroup(panel_layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panel_layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(temp_par_group)
                    .addContainerGap()));
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        // not sure there is anything to do here...
    }

    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        int i;
        for (i = 0; i < e.getLength(); i++) {
            this.add_combo_box (e.getOffset(),  new String[] { e.toString(), "a", "b", "c" });
        }
    }

    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        int i;
        for (i = 0; i < e.getLength(); i++) {
            this.remove_combo_box(e.getOffset());
        }
    }

}
