package com.lestarch.autoclick;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Frame (UI) for the auto-clicking program 
 * 
 * @author starchmd
 */
public class AutoClickFrame extends JFrame {
    //Unused version stamp -- Version 1 compatibility
    private static final long serialVersionUID = 1L;
    
    private static final String CLICK_INTERVAL = "Click Interval:";
    private static final String RANDOM_VARIATION = "Random Variation";
    private static final String SELECT_TARGET = "Enter Target Select";
    private static final String CLICK_TO_START = "Click Target to Start";
    private static final String CLICK_TO_STOP = "Press F10 to Stop";
    private static final String TITLE = "石－自動按";
    private ClickingRobot robot;

    /**
     * Constructor
     */
    public AutoClickFrame() {
        try {
            this.robot = new ClickingRobot();
        } catch(AWTException e) {
            dialogError("Failed to start clicking-robot: "+e);
        }
    }
    /**
     * Builds the UI
     */
    public void build() {
        //Components of UI
        final JPanel pContent = new JPanel();
        final JLabel lSpeed = new JLabel(CLICK_INTERVAL);
        NumberFormat format = NumberFormat.getIntegerInstance();
        final JFormattedTextField tfSpeed = new JFormattedTextField(format);
        tfSpeed.setValue(new Integer(this.robot.getInterval()));
        final JCheckBox cbJitter = new JCheckBox(RANDOM_VARIATION);
        cbJitter.setSelected(this.robot.getJitter());
        final JButton bStart = new JButton(SELECT_TARGET);
        //Build Layout
        GridBagLayout layout = new GridBagLayout();
        pContent.setLayout(layout);
        GridBagConstraints gc = new GridBagConstraints();
        //Add first line
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        gc.gridheight = 1;
        pContent.add(lSpeed,gc);
        gc.gridx = 3;
        gc.gridwidth = 1;
        pContent.add(tfSpeed, gc);
        //Second line, Third lines
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        pContent.add(cbJitter, gc);
        gc.gridy = 2;
        pContent.add(bStart, gc);
        this.add(pContent);
        //Set Frame Properties
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        //Action bindings
        tfSpeed.addPropertyChangeListener("value",new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                AutoClickFrame.this.robot.setInterval(getNewValue());
            }
            /**
             * Gets the value as an integer
             * @return integer of value
             */
            private int getNewValue() {
                return Integer.parseInt(tfSpeed.getValue().toString());
            }

        });
        cbJitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AutoClickFrame.this.robot.setJitter(cbJitter.isSelected());
            }    
        });
        final StateMachine machine = new StateMachine(bStart,this.robot);
        machine.pump();
        bStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                machine.pump();
            }
        });
    }
    /**
     * Starts the program
     */
    public void start() {
        this.build();
        this.pack();
        this.setVisible(true);
    }
    /**
     * Display an error
     * @param error - error message to display
     */
    private void dialogError(String error) {
        try {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            System.err.println("Error: "+error);
        }
    }
    /**
     * A state machine changing the button state
     * @author starchmd
     */
    static class StateMachine implements AWTEventListener {
        JButton button;
        ClickingRobot robot;
        Thread runner;

        State current = State.STOPPED;
        /**
         * Construct this state machine
         * @param button - button moving between states
         */
        public StateMachine(JButton button,ClickingRobot robot) {
            this.button = button;
            this.robot = robot;
            this.runner = new Thread(this.robot);
            Toolkit.getDefaultToolkit().addAWTEventListener(this,AWTEvent.FOCUS_EVENT_MASK);
        }
        /**
         * Run one cycle of the state machine
         */
        public void pump() {
            switch (this.current) {
                case STOPPED:
                    this.button.setText(SELECT_TARGET);
                    this.button.setEnabled(true);
                    this.current = State.LOCATION;
                    break;
                case LOCATION:
                    this.button.setText(CLICK_TO_START);
                    this.button.setEnabled(false);
                    this.current = State.STARTED;
                    break;
                case STARTED:
                    this.button.setText(CLICK_TO_START);
                    this.button.setEnabled(false);
                    this.current = State.STOPPED;
                    break;
            }
        }
        @Override
        public void eventDispatched(AWTEvent event) {
            Focu
            System.out.println("Event:"+event);
            if (event instanceof FocusEvent && this.current == State.LOCATION) {
                this.robot.setLocation(MouseInfo.getPointerInfo().getLocation());
                //this.runner.start();
            }
        };
        /**
         * Possible states
         * @author starchmd
         */
        enum State {
            STOPPED,
            LOCATION,
            STARTED
        }
    }
    /**
     * Starts the main program
     * @param args - arguments
     */
    public static void main(String[] args) {
        AutoClickFrame autoClick = new AutoClickFrame();
        autoClick.start();
    }
}
