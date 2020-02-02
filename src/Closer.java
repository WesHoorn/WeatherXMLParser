import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Closer implements Runnable {

    private boolean runbool = true;
    private String error = "This is an error placeholder";
    private JLabel errorlabel;


    public Closer(){
        makeWindow();
    }

    private void makeWindow(){
        JFrame f=new JFrame("Weatherdata server");
        JLabel l = new JLabel("The weatherdata server is currently running");
        this.errorlabel = new JLabel(this.error);
        errorlabel.setBounds(0,100,150, 50);
        JButton b=new JButton("Shutdown");
        b.setBounds(125,200,150, 50);
        l.setBounds(0,0, 300, 50);
        f.add(b);
        f.add(l);
        f.add(errorlabel);
        f.setSize(400,300);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                setRunBool();
                setError("Exiting...");
                updateError();
            }
        });}

    private void setRunBool(){
        this.runbool = false;
    }

    public Boolean getBool(){
        return runbool;
    }

    public void setError(String error){
        this.error = error;
    }

    private void updateError(){
        errorlabel.setText(error);
    }


    @Override
    public void run() {
        System.out.println("Closer running");
        while (runbool){
            try{
                Thread.sleep(5000);}
            catch(InterruptedException e){
                e.printStackTrace();
                System.out.println("Swing thread failed to sleep");
            }
            updateError();
        }
    }
}
