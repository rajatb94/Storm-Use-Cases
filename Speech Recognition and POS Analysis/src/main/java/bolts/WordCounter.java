package bolts;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;


//--------for lingpipe

import com.aliasi.classify.ConditionalClassification;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;

import com.aliasi.symbol.SymbolTable;

import com.aliasi.tag.TagLattice;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tag.Tagging;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;

import com.aliasi.util.ScoredObject;
import com.aliasi.util.Streams;
import com.aliasi.util.Strings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;




public class WordCounter extends BaseBasicBolt {
private static String data="";
	Map myMap = new HashMap<String, String>();
    public WordCounter(){
        
    	myMap.put("ABL", "pre-qualifier");
    	myMap.put("ABN", "pre-qun=antifier");
    	myMap.put("ABX", "pre-quantifier");
    	myMap.put("AP", "post-determiner");
    	myMap.put("AP$", "possessive post-determiner");
    	myMap.put("AT", "article");
    	myMap.put("BE", "be");
    	myMap.put("BED", "were");
    	myMap.put("BEDZ", "was");
    	myMap.put("BEG", "being");
    	myMap.put("BEM", "am");
    	myMap.put("BEN", "been");
    	myMap.put("BER", "are,art");
    	myMap.put("BEZ", "is");
        myMap.put("CC", "Coordinating conjunction");
        myMap.put("CD", "Cardinal number");
        myMap.put("CD$", "possessive Cardinal numeral");
        myMap.put("CS", "subordinating conjunction");
        myMap.put("DO", "do");
        myMap.put("DOD", "did");
        myMap.put("DOZ", "does");
        myMap.put("DT", "Determiner");
        myMap.put("DT$", "possessive singular Determiner");
        myMap.put("DTI", "singular or plural Determiner/quantifier");
        myMap.put("DTS", "plural Determiner");
        myMap.put("DTX", "Determiner/double junction");
        myMap.put("EX", "Existential there");
        myMap.put("FW", " Foreign word");
        myMap.put("HV", "have");
        myMap.put("HVD", "had(past tense)");
        myMap.put("HVG", "having");
        myMap.put("HVN", "had(past particle)");
        myMap.put("HVZ", "has");
        myMap.put("IN", "Preposition or subordinating conjunction");
        myMap.put("JJ", "Adjective");
        myMap.put("JJ$", "possessive adjective");
        myMap.put("JJR", "Adjective, comparative");
        myMap.put("JJS", "Adjective, superlative");
        myMap.put("JJT", "morphologically superlative adjective");
        myMap.put("LS", "List item marker");
        myMap.put("MD", "Modal");
        myMap.put("NIL", "no category assigned");
        myMap.put("NN", "Noun, singular or mass");
        myMap.put("NN$", "possessive singular noun");
		myMap.put("NNP", "Proper noun, singular");
        myMap.put("NNPS", "Proper noun, plural");
        myMap.put("NNS", "Noun, plural");
        myMap.put("NNS$", "possessive plural noun");
        myMap.put("NP", "proper noun or part of name phrase");
        myMap.put("NP$", "possessive proper noun");
        myMap.put("NPS", "plural proper noun");
        myMap.put("NPS$", "possessive proper noun");
        myMap.put("NR", "adverbial noun");
        myMap.put("NR$", "possessive adverbial noun");
        myMap.put("NRS", "plural adverbial noun");
        myMap.put("OD", "ordinal numeral");
        myMap.put("PDT", "Predeterminer");
        myMap.put("PN", "nominal pronoun");
        myMap.put("PN$", "possessive nominal pronoun");
        myMap.put("POS", "Possessive ending");
        myMap.put("PP$", "possessive personal pronoun");
        myMap.put("PP$$", "second(nominal) possessive pronoun");
        myMap.put("PPL", "singular reflexive/intensive Personal pronoun");
        myMap.put("PPLS", "plural reflexive/intensive Personal pronoun");
        myMap.put("PPO", "objective Personal pronoun");
        myMap.put("PPS", "3rd. singular nominative pronoun");
        myMap.put("PPSS", "other nominative Personal pronoun");
        myMap.put("PRP", "Personal pronoun");
        myMap.put("PRP$", "Possessive pronoun");
        myMap.put("QL", "qualifier");
        myMap.put("QLP", "post-qualifier");
        myMap.put("RB", "Adverb");
        myMap.put("RB$", "possessive adverb");
        myMap.put("RBR", "Adverb, comparative");
        myMap.put("RBS", "Adverb, superlative");
        myMap.put("RBT", "superlative adverb");
        myMap.put("RN", "nominal adverb");
        myMap.put("RP", "Particle");
        myMap.put("SYM", "Symbol");
        myMap.put("TO", "to");
        myMap.put("UH", "Interjection");
        myMap.put("VB", "Verb, base form");
        myMap.put("VBD", "Verb, past tense");
        myMap.put("VBG", "Verb, gerund or present participle");
        myMap.put("VBN", "Verb, past participle");
        myMap.put("VBP", "Verb, non­3rd person singular present");
        myMap.put("VBZ", "Verb, 3rd person singular present");        
        myMap.put("WDT", "Wh­determiner");
        myMap.put("WP", "Wh­pronoun");
        myMap.put("WP$", "Possessive wh­pronoun");
        myMap.put("WPO", "objective wh-pronoun");
        myMap.put("WPS", "nominative wh-pronoun");
        myMap.put("WQL", "wh-qualifier");
        myMap.put("WRB", "Wh­adverb");
    }

	private static int max = 0;
	private static String trendin = null;
        
	public static TokenizerFactory TOKENIZER_FACTORY = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
	Integer id;
	String name;
	Map<String, Integer> counters;

	/**
	 * At the end of the spout (when the cluster is shutdown
	 * We will show the word counters
	 */
	@Override
	public void cleanup() {}

	/**
	 * On create 
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		this.counters = new HashMap<String, Integer>();
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
               
                
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}


	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
            data="";
		String str = input.getString(0);

		try{
			InputStream fileIn = getClass().getResourceAsStream("/pos-en-general-brown.HiddenMarkovModel");
        	ObjectInputStream objIn = new ObjectInputStream(fileIn);
        	HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
        	Streams.closeQuietly(objIn);
        	HmmDecoder decoder = new HmmDecoder(hmm);

  		    char[] cs = str.toCharArray();

            Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs,0,cs.length);
            String[] tokens = tokenizer.tokenize();
            List<String> tokenList = Arrays.asList(tokens);
            Tagging<String> tagging = decoder.tag(tokenList);
        	for (int i = 0; i < tagging.size(); ++i){
            	//data=data + ("\"" + tagging.token(i) + "\":\"" + myMap.get(tagging.tag(i).toUpperCase()) + "\",");
                data=data + ("{\"token\":\"" + tagging.token(i) + "\", \"type\":\"" + myMap.get(tagging.tag(i).toUpperCase()) + "\"},");
            }
        }
        catch(Exception e){
        	System.out.println("Exception: " + e);
        }
                 
                 WebSocketClient mWs;
            try {
                mWs = new WebSocketClient( new URI( "ws://192.168.1.23:9999/said" ), new Draft_10() ){
                    
                    @Override
                    public void onMessage( String message ) {
                        
                        
                    }
                    
                    @Override
                    public void onOpen( ServerHandshake handshake ) {
                        System.out.println( "opened connection bolt" );
                        data="[" + data + "]";
                        this.send("{\"client\": \"bolt\",\"data\": \"" + JSONObject.escape(data) + "\"}");
                    }
                    
                    @Override
                    public void onClose( int code, String reason, boolean remote ) {
                        System.out.println( "closed connection bolt" );
                    }
                    
                    @Override
                    public void onError( Exception ex ) {
                        ex.printStackTrace();
                        System.out.println("zzzzzzzzzzzzzzzz");
                    }
                };
                System.out.println("xxxxxxxxxxxxxxxx");
                mWs.connect();
                System.out.println("yyyyyyyyyyyyyyyy");
               
                //mWs.close();
            } catch (URISyntaxException ex) {
                Logger.getLogger(WordCounter.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                
	}
}
