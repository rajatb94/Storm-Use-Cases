import spouts.Twitter;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.StormSubmitter;
import bolts.WordCounter;
import bolts.WordNormalizer;


public class TopologyMain {
	public static void main(String[] args) throws InterruptedException{
         
        //Topology definition
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader",new Twitter());
		builder.setBolt("word-normalizer", new WordNormalizer())
			.shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCounter(),1)
			.fieldsGrouping("word-normalizer", new Fields("word"));
		
        //Configuration
		Config conf = new Config();
		conf.setNumWorkers(1);
		conf.setMaxSpoutPending(1);
		conf.setDebug(false);
		try{
			StormSubmitter.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		}
		catch(Exception e){
			System.out.println("ERROR: " + e);
		}

		
        //Topology run
		//conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		//LocalCluster cluster = new LocalCluster();
		//cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		//Thread.sleep(1000);
		//cluster.shutdown();
	}
}
