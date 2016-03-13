package server;

import java.util.EventListener;

/**
 * Created by emilio on 26/01/16.
 */
public interface UpdateViewListener extends EventListener{
    public void onRequestUpdateViewLog(UpdateViewEvent e);
    public void onRequestUpdateViewClients(UpdateViewEvent e);
}
