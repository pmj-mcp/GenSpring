import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class GenSpringGui {

   final JTextField tfProject = new JTextField("com.examples.myproject");
   final JTextField tfPath = new JTextField("./com/examples/myproject");
   final JTextField tfEntity = new JTextField("Customer");
   final JButton btnGenerate = new JButton("Generate");
   final JLabel lbResult = new JLabel("<html><font color=\"blue\">En<br/> attente</font></html>");

   public GenSpringGui() {
      super();
      createWindow();
   }

   public static void main(String[] args) {
      new GenSpringGui();
   }

   private void createWindow() {    
      JFrame frame = new JFrame("Spring Squeleton Entity Generator");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      createUI(frame);
      frame.setSize(560, 200);      
      frame.setLocationRelativeTo(null);  
      frame.setVisible(true);
   }

   private  void createUI(final JFrame frame){  
      JPanel panel = new JPanel();
      LayoutManager layout = new GridBagLayout();  
      panel.setLayout(layout);       
      GridBagConstraints gbc = new GridBagConstraints();

      tfPath.setColumns(255);

      tfPath.setMinimumSize(new Dimension(200, 30));
      URL img = GenSpringGui.class.getResource("folder-blue-java-icon.16.png");
      //URL img = SwingTester.class.getResource("folder-blue-java.svg");
      JButton button = new JButton(new ImageIcon(img));
      //final JLabel label = new JLabel();

      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(frame);
            if(option == JFileChooser.APPROVE_OPTION){
               File file = fileChooser.getSelectedFile();
               tfPath.setText(file.getAbsolutePath());
               lbResult.setText(lbResult.getText().replace("</html>","<br />"+file.getAbsolutePath()+"</html>"));
               //tfPath.setPreferredSize(new Dimension(100,tfPath.getHeight()));
               btnGenerate.setEnabled(true);
            }else{
               tfPath.setText(null);
               btnGenerate.setEnabled(false);
            }
         }
      });

      //JProgressBar pb = new JProgressBar(0, 5);
      //pb.setStringPainted(true);
      btnGenerate.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            //pb.setValue(0);
            //pb.setVisible(true);
            lbResult.setText("<html>Generate "+tfEntity.getText()+" to " + tfPath.getText() +"...</html>");
            String targetdir = tfPath.getText().replace("\\","/");
            new GenSpring(tfProject.getText(), tfEntity.getText(), targetdir);
            lbResult.setText(lbResult.getText().replace("...</html>","<br /> Generated</html>"));
            //pb.setValue(5);
         }
      });

      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridx = 0;
      gbc.gridy = 0;
      panel.add(new JLabel("Package : "), gbc);
      ++gbc.gridx;
      gbc.gridwidth=2;
      panel.add(tfProject,gbc);
      //gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth=1;
      gbc.gridx = 0;
      ++gbc.gridy;
      panel.add(new JLabel("Target directory : "), gbc);
      //gbc.fill = GridBagConstraints.HORIZONTAL;
      ++gbc.gridx;
      //gbc.gridy = 0;
      panel.add(tfPath, gbc);
      //gbc.fill = GridBagConstraints.HORIZONTAL;
      ++gbc.gridx;
      //gbc.gridy = 0;
      panel.add(button,gbc);
      gbc.gridx = 0;
      ++gbc.gridy;
      panel.add(new JLabel("Entity : "), gbc);
      ++gbc.gridx;
      gbc.gridwidth=2;
      panel.add(tfEntity,gbc);
      ++gbc.gridy;
      gbc.gridx = 0;
      gbc.gridwidth=3; 
      panel.add(btnGenerate,gbc);
      // ++gbc.gridy;
      // gbc.gridx = 0;
      // gbc.gridwidth=3;
      // pb.setVisible(false); 
      // panel.add(pb,gbc);
      gbc.fill = GridBagConstraints.BOTH;
      ++gbc.gridy;
      gbc.gridheight=5;
      panel.add(lbResult,gbc);
      
      frame.getContentPane().add(panel, BorderLayout.CENTER);    
   }  
}