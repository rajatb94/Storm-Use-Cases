package spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.drafts.Draft_10;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

 
public class Voicerec extends BaseRichSpout{

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    public void ack(Object msgId) {
        //System.out.println("OK:"+msgId);
    }
    public void close() {}
    public void fail(Object msgId) {
        System.out.println("FAIL:"+msgId);
    }

    public void nextTuple() {
        /**
         * The nextuple it is called forever, so if we have been readed the file
         * we will wait and then return
         */
        if(completed){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Do nothing
            }
            return;
        }
    }

    /**
     * We will create the file and get the collector object
     */
    public void open(Map conf, TopologyContext context, final SpoutOutputCollector collector) {
        /*try {
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
        }*/
        WebSocketClient mWs;
        try {
                mWs = new WebSocketClient( new URI( "ws://192.168.1.23:9999/said" ), new Draft_10() ){
                
                @Override
                public void onMessage( String message ) {
                    collector.emit(new Values(message),message);
                }
                
                @Override
                public void onOpen( ServerHandshake handshake ) {
                    System.out.println( "opened connection" );
                }
                
                @Override
                public void onClose( int code, String reason, boolean remote ) {
                    System.out.println( "closed connection" );
                }
                
                @Override
                public void onError( Exception ex ) {
                    ex.printStackTrace();
                }
            };
            mWs.connect();
        }

        catch(Exception e){
            System.out.println("ERROR: " + e);
        }  
        this.collector = collector;
    }

    /**
     * Declare the output field "line"
     */

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }


   
}