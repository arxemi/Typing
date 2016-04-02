package client;
import javax.swing.*;
import java.awt.*;


/**
 * Created by kalix on 3/23/16.
 */
public class CustomListCell extends JLabel implements ListCellRenderer {

    public CustomListCell() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Assumes the stuff in the list has a pretty toString
        setText(value.toString());

        // based on the index you set the color.  This produces the every other effect.
        if (index % 2 == 0) setBackground(Color.WHITE);
        else setBackground(new Color(198, 198, 198));

        return this;
    }

    @Override
    public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
