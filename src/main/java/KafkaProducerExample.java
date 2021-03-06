
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.Map;
import java.util.Properties;
public class KafkaProducerExample {
    private final static String TOPIC = "dharshini";

    // NOTE: you only need to specify one bootstrap server
    // the others will be found out automatically
    // the benefit off adding more than one is in case
    // one of them is down
    private final static String BOOTSTRAP_SERVERS =
            "localhost:9092";
    //,localhost:9093,localhost:9094";





    private static Producer<Long, MessagePayload> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());

        System.out.println(MessagePayloadSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                MessagePayloadSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static void main(String[] args) throws Exception{


        System.out.println("boom");

        if (args.length == 0) {
            runProducer(5);
        } else {
            runProducer(Integer.parseInt(args[0]));
        }
    }

    static void runProducer(final int sendMessageCount) throws Exception {
        final Producer<Long, MessagePayload> producer = createProducer();
        long time = System.currentTimeMillis();
        try {
            for (long index = time; true; index++) {

                Thread.sleep(1000);

                final ProducerRecord<Long, MessagePayload> record =
                        new ProducerRecord<>(TOPIC, index,
                                new MessagePayload(("bread at time "+System.currentTimeMillis()),"buy"));
                RecordMetadata metadata = producer.send(record).get();
                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("sent record(key=%s value=%s) " +
                                "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }



}