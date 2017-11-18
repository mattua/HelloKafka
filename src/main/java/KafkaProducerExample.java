
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
    private final static String TOPIC = "test";
    private final static String BOOTSTRAP_SERVERS =
            "localhost:32785";
    //,localhost:9093,localhost:9094";

    public static class MessageMatt {

        private String object;

        private String verb;

        public MessageMatt(String object,String verb){

            this.object=object;
            this.verb=verb;
        }

        public String toString(){
            return verb + " to " + object;
        }


    }


    public static class MessageMattSerializer implements Serializer<MessageMatt> {



        public MessageMattSerializer(){

        }

        @Override public void configure(Map<String, ?> map, boolean b) {

        }

        @Override

        public byte[] serialize<MessageMatt>(String arg0, MessageMatt arg1) {
            byte[] retVal = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                retVal = objectMapper.writeValueAsString(arg1).getBytes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return retVal;
        }

        @Override public void close() {

        }

    }



    private static Producer<Long, MessageMatt> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                MessageMattSerializer.class.getName());
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
        final Producer<Long, MessageMatt> producer = createProducer();
        long time = System.currentTimeMillis();
        try {
            for (long index = time; true; index++) {

                Thread.sleep(3000);

                final ProducerRecord<Long, MessageMatt> record =
                        new ProducerRecord<>(TOPIC, index,
                                new MessageMatt("milk","buy"));
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