// NetworkManager.java
// A class that handles socket shenanigans.

package vsthespire.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class NetworkManager {

    public final int CONNECTWAITTIME = 5000;

    private ServerSocket server;
    private Socket mySocket;
    private InetAddress theirIP;
    private int portNum;

    private boolean isServer;

    //fills stuff in so they aint empty
    public NetworkManager() {
        //Don't know why empty ones would fail, but okay
        try {
            this.server = new ServerSocket();
            this.mySocket = new Socket();
            this.theirIP = InetAddress.getLoopbackAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.portNum = 9999;
        this.isServer = false;
    }

    //called at start of run using config settings
    //returns true on success
    public boolean initialize(String ip, String port) {
        boolean isSuccess = true;
        if(ip.equalsIgnoreCase("host")) {
            isServer = true;
        }
        else {
            isServer = false;
            try {
                theirIP = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                isSuccess = false;
            }
        }
        try {
            this.portNum = Integer.parseInt(port);
        }
        catch(NumberFormatException e) {
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean isServer() {
        return isServer;
    }

    //Attempt to bind to the port and wait for a client to connect
    //returns true on success
    public boolean tryBind() {
        boolean isBound = false;
        if(isServer) {
            isBound = true;
            try {
                server = new ServerSocket(portNum, 1, null);
                server.setSoTimeout(CONNECTWAITTIME);
            } catch (IOException e) {
                isBound = false;
            }
        }
        return isBound;
    }

    //The host becomes available to connect to until timeout
    //returns true on success
    public boolean tryAccept() {
        boolean connected = false;
        if(isServer) {
            connected = true;
            try {
                mySocket = server.accept();
                mySocket.setSoTimeout(CONNECTWAITTIME);
            }
            catch(IOException e) {
                connected = false;
            }
        }
        return connected;
    }

    //The client attempts to connect to a host
    //returns true on success
    public boolean tryConnect() {
        boolean connected = false;
        if(!isServer) {
            connected = true;
            try {
                mySocket = new Socket(theirIP, portNum);
                mySocket.setSoTimeout(CONNECTWAITTIME);
            }
            catch(IOException e) {
                connected = false;
            }
        }
        return connected;
    }

    public BufferedReader getInput() throws IOException{
        return new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
    }

    public PrintWriter getOutput() throws IOException{
        return new PrintWriter(mySocket.getOutputStream());
    }

    //Closes the sockets
    public void close() {
        if(server.isBound()){
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(mySocket.isConnected()){
            try {
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
