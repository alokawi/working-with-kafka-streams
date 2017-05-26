package alokawi.kafkastreams.core;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

public class KafkaStreamsExample {

	// Serializers/deserializers (serde) for String and Long types
	final Serde<String> stringSerde = Serdes.String();
	final Serde<Long> longSerde = Serdes.Long();

	public static void main(String[] args) {

		KafkaStreamsExample example = new KafkaStreamsExample();
		example.run();

	}

	@SuppressWarnings("unused")
	private void run() {

		// Construct a `KStream` from the input topic ""streams-file-input",
		// where message values
		// represent lines of text (for the sake of this example, we ignore
		// whatever may be stored
		// in the message keys).
		KStreamBuilder builder = new KStreamBuilder();
		KStream<String, Long> streamFromTestTopic = builder.stream("test");

	}

}
