package server.listener_interface;

import server.UpdateViewEvent;

import java.util.EventListener;

/**
 * Created by emilio on 26/01/16.
 */
public interface UpdateViewListener extends EventListener{
    void onRequestUpdateViewLog(UpdateViewEvent e);
    void onRequestUpdateViewClients(UpdateViewEvent e);
}
