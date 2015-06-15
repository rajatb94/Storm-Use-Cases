package bolts;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;


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
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



//--------------



public class WordCounter extends BaseBasicBolt {

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
	public void cleanup() {
		System.out.println("-- Word Counter ["+name+"-"+id+"] --");
		for(Map.Entry<String, Integer> entry : counters.entrySet()){
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
	}

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
        		if(tagging.tag(i).equals("np")){
            		System.out.print("proper noun --->  " + tagging.token(i) + '\t' + '\t');
            		if(!counters.containsKey(str)){
						counters.put(str, 1);
					}else{
						Integer c = counters.get(str) + 1;
						counters.put(str, c);
					}
					if(counters.get(str)>=max){
						trendin = str;
						max = counters.get(str);
					}
					if(trendin!=null){
						System.out.println("trending : " + trendin);
					}
        		}
        	}
        }
        catch(Exception e){
        	System.out.println("Exception: " + e);
        }
	}
}
