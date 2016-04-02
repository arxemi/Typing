package client;

import java.util.EventListener;

/**
 * Created by kalix on 3/25/16.
 */
public interface ClientServiceListener extends EventListener{
    void onReciveMessageFromServer(ClientServiceEvent e);
    void addClientToList(ClientServiceEvent e);
    void removeClientFromList(ClientServiceEvent e);
}
