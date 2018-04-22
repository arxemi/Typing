package server.listener;

import server.UpdateViewEvent;

import java.util.EventListener;

/**
 * @author emilio acciaro on 26/01/16.
 */
public interface UpdateViewListener extends EventListener{
    void onRequestUpdateViewLog(UpdateViewEvent e);
    void onRequestUpdateViewClients(UpdateViewEvent e);
}
