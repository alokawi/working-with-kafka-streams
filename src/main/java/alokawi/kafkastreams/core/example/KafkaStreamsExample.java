package alokawi.kafkastreams.core.example;

import java.util.Arrays;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

public class KafkaStreamsExample {

	// Serializers/deserializers (serde) for String and Long types
	final Serde<String> stringSerde = Serdes.String();
	final Serde<Long> longSerde = Serdes.Long();

	public static void main(String[] args) {

		KafkaStreamsExample example = new KafkaStreamsExample();
		example.run();

	}

	private void run() {

		// Construct a `KStream` from the input topic ""streams-file-input",
		// where message values
		// represent lines of text (for the sake of this example, we ignore
		// whatever may be stored
		// in the message keys).
		KStreamBuilder builder = new KStreamBuilder();
		KStream<String, String> textLines = builder.stream(stringSerde, stringSerde, "streams-file-input");

		KTable<String, Long> wordCounts = textLines
				// Split each text line, by whitespace, into words.
				.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))

				// Group the text words as message keys
				.groupBy((key, value) -> value)

				// Count the occurrences of each word (message key).
				.count("Counts");

		// Store the running counts as a changelog stream to the output topic.
		wordCounts.to(stringSerde, longSerde, "streams-wordcount-output");

	}
}
