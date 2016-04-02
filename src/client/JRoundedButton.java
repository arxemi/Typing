package client;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by kalix on 3/24/16.
 */
public class JRoundedButton extends JComponent {

    public void paint(Graphics g) {


        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        // draw the perimeter of the button
        g.setColor(getBackground().darker().darker().darker());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

    }

}
