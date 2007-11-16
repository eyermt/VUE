
/*
 *
 * * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2003-2007
 * Tufts University. All rights reserved.</p>
 *
 *
 */

package edu.tufts.vue.ontology.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.Border;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

//not currently needed: VUE-815
//import tufts.vue.VueConstants;
import tufts.vue.DataSourceViewer;
import tufts.vue.FavoritesDataSource;
import tufts.vue.FavoritesWindow;
import tufts.vue.LWComponent;
import tufts.vue.LWImage;
import tufts.vue.LWNode;
import tufts.vue.LWSelection;
import tufts.vue.LWSlide;
import tufts.vue.NodeTool;
import tufts.vue.VUE;
import tufts.vue.gui.CheckBoxRenderer;
import tufts.vue.gui.GUI;
import tufts.vue.gui.WindowDisplayAction;
import tufts.vue.ui.ResourceIcon;


/**
 *
 * OntologyList.java
 *
 * Created on April 9, 2007, 11:36 AM
 *
 * @author dhelle01
 */
public class OntologyList extends javax.swing.JList implements OntologySelectionListener,
																MouseListener, ActionListener
{
	
    public OntologyList(OntologyViewer viewer)
    {
        super(new OntologyListModel());
        setCellRenderer(new OntologyListRenderer());
        addMouseListener(this);
    }
    
    public void refresh()
    {
        ((OntologyListModel)getModel()).refresh();
    }
    
    public void ontologySelected(OntologySelectionEvent ose)
    {   
        edu.tufts.vue.ontology.Ontology ont = ose.getSelection().getOntology();
        
        for(int i=0;i<getModel().getSize();i++)
        {
            if(getModel().getElementAt(i).equals(ont))
                setSelectedIndex(i);
        }   
    }
    
    public static class OntologyListModel extends javax.swing.DefaultListModel
    {
        
        public Object getElementAt(int index)
        {
            if(index > edu.tufts.vue.ontology.OntManager.getOntManager().getOntList().size() - 1 )
                return null;
            else
                return edu.tufts.vue.ontology.OntManager.getOntManager().getOntList().get(index);
        }
        
        public int getSize()
        {
            return edu.tufts.vue.ontology.OntManager.getOntManager().getOntList().size();
        }
        
        public void refresh()
        {
            fireContentsChanged(this,0,getSize());
        }
        
    }
    
    public static class OntologyListRenderer extends javax.swing.DefaultListCellRenderer
    {
        private Border dividerBorder = new tufts.vue.gui.DashBorder(java.awt.Color.LIGHT_GRAY,false,true,false,false);
        private Border emptyBorder = javax.swing.BorderFactory.createEmptyBorder();

       //currently disabled as per VUE-815 
       //private javax.swing.JLabel importLabel = new javax.swing.JLabel("Add Style Sheet");
        
        private CheckBoxRenderer mCheckBox = new CheckBoxRenderer();
        
        private JPanel renderer;
        private JLabel label;
        
        public OntologyListRenderer()
        {
            renderer = new JPanel()
            {
               public java.awt.Dimension getPreferredSize()
               {
                   return new java.awt.Dimension(100,23);
               }
            };
            
            renderer.setLayout(new javax.swing.BoxLayout(renderer,javax.swing.BoxLayout.X_AXIS));
            
            renderer.setOpaque(true);
            
            label = new JLabel();
            
            renderer.add(Box.createHorizontalStrut(GUI.WidgetInsets.left));
            renderer.add(mCheckBox);           
            renderer.add(Box.createHorizontalStrut(GUI.WidgetInsets.left));
            renderer.add(label);
        }

        public java.awt.Component getListCellRendererComponent(javax.swing.JList list,Object value,int index,boolean isSelected,boolean hasFocus)
        {	
            //currently disabled as per VUE-815
            //importLabel.setFont(VueConstants.FONT_NARROW);
            
            if(index!=0)
              renderer.setBorder(dividerBorder);
            else
              renderer.setBorder(emptyBorder);
            
            edu.tufts.vue.ontology.Ontology ontology = (edu.tufts.vue.ontology.Ontology)value;

            label.setText(ontology.getLabel());
            
            mCheckBox.setSelected(ontology.isEnabled());
     
            if(value == list.getSelectedValue())
            {
                renderer.setBackground(tufts.vue.gui.GUI.getTextHighlightColor());
            }
            else
            {
                renderer.setBackground(new java.awt.Color(255,255,255));
            }
            
            // next lines left here in case we ever want to add the import label back in 
            // note: also uncomment lines out above- search for VUE-815
            /*if(ontology.getStyle() == null)
            {   
                panel.add(javax.swing.Box.createHorizontalGlue());
                importLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,5));
                panel.add(importLabel);
            }*/
            return renderer;
        }
    }

    private void displayContextMenu(MouseEvent e) {
        getPopup(e).show(e.getComponent(), e.getX(), e.getY());
	}
	
	JPopupMenu m = null;
	private static final JMenuItem addStyleSheet = new JMenuItem("Add Style Sheet");
    private static final JMenuItem deleteOntology = new JMenuItem("Delete Ontology");
    
	private JPopupMenu getPopup(MouseEvent e) 
	{
		if (m == null)
		{
			m = new JPopupMenu("Resource Menu");
		
			m.add(addStyleSheet);
			m.add(deleteOntology);
			addStyleSheet.addActionListener(this);
			deleteOntology.addActionListener(this);
		}
		int index = this.locationToIndex(lastMouseClick);
		edu.tufts.vue.ontology.Ontology o = (edu.tufts.vue.ontology.Ontology)this.getModel().getElementAt(index);
		if (o.getCssFileName() != null)
			addStyleSheet.setLabel("Replace Style Sheet");
		else
			addStyleSheet.setLabel("Add Style Sheet");
		
		return m;
	}
	Point lastMouseClick = null;
	
	public void mouseClicked(MouseEvent arg0) {
		 
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(addStyleSheet))
		{
			OntologyBrowser.getBrowser().applyStyle.actionPerformed(e);
		} else if (e.getSource().equals(deleteOntology))
		{
			OntologyBrowser.getBrowser().removeOntology.actionPerformed(e);
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger())
		 {
			 	lastMouseClick = e.getPoint();
				displayContextMenu(e);
		 }
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
		 {
			 	lastMouseClick = e.getPoint();
				displayContextMenu(e);
		 }
	}

}
