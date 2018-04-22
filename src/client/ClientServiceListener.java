package client;

import java.util.EventListener;

/**
 * @author emilio acciaro on 3/25/16.
 */

public interface ClientServiceListener extends EventListener{
    void onReceiveCriticalMessageFromServer(ClientServiceEvent e);
    void onReceiveMessageFromServer(ClientServiceEvent e);
    void addClientToList(ClientServiceEvent e);
    void removeClientFromList(ClientServiceEvent e);
}
