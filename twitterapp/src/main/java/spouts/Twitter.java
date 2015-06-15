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
import org.json.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

 
public class Twitter extends BaseRichSpout{

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/sample.json"; //visit https://dev.twitter.com for more streaming api options
    private static final String API_KEY = ""; //get these keys after making a twitter app 
    private static final String API_SECRET = "";
    private static final String ACCESS_TOKEN = "";
    private static final String ACCESS_TOKEN_SECRET = "";
    public void ack(Object msgId) {
        System.out.println("OK:"+msgId);
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
        

        try{

            OAuthService service = new ServiceBuilder()
                           .provider(TwitterApi.class)
                           .apiKey(API_KEY)
                           .apiSecret(API_SECRET)
                           .build();

            Token accessToken = new Token( ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
            OAuthRequest request = new OAuthRequest(Verb.POST, STREAM_URI);
            request.setConnectionKeepAlive(true);
            //request.addBodyParameter("track", "narendra,modi"); //visit https://dev.twitter.com for more streaming api options
            request.addBodyParameter("language", "en");
            service.signRequest(accessToken, request);
            Response response = request.send();
        
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getStream()));

            String inputLine;
            
            //Read all lines
            while((inputLine = in.readLine()) != null){

                JSONObject strobj = new JSONObject(inputLine);
                if(strobj.has("text")){
                    String str = strobj.getString("text");
                    this.collector.emit(new Values(str),str);
                    
                }
            }




        }catch(Exception e){
            throw new RuntimeException("Error reading tuple",e);
        }finally{
            completed = true;
        }
    }

    /**
     * We will create the file and get the collector object
     */
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        
        this.collector = collector;
    }

    /**
     * Declare the output field "word"
     */


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }

   
}