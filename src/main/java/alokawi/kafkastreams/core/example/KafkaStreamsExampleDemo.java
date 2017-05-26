/**
 * 
 */
package alokawi.kafkastreams.core.example;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

/**
 * @author alokkumar
 *
 */
public class KafkaStreamsExampleDemo {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
		streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		final Serde<String> stringSerde = Serdes.String();
		final Serde<Long> longSerde = Serdes.Long();

		KStreamBuilder builder = new KStreamBuilder();
		KStream<String, String> textLines = builder.stream(stringSerde, stringSerde, "TextLinesTopic");
		KStream<String, Long> wordCounts = textLines
				.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
				.map((key, word) -> new KeyValue<>(word, word))
				// Required in Kafka 0.10.0 to re-partition the data because we
				// re-keyed the stream in the `map` step.
				// Upcoming Kafka 0.10.1 does this automatically for you (no
				// need for `through`).
				.through("RekeyedIntermediateTopic").groupByKey().count("Counts").toStream();
		wordCounts.to(stringSerde, longSerde, "WordsWithCountsTopic");

		KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

}
