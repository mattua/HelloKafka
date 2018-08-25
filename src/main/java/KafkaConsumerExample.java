
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class KafkaConsumerExample {


    public static void main(String[] args) {
        int numConsumers = 1;
        String groupId = "cgroup"+System.currentTimeMillis();

        groupId="cgroup1535191753571";
        List<String> topics = Arrays.asList("dharshini");
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<LoopingConsumer> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            LoopingConsumer consumer = new LoopingConsumer(i, groupId, topics);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (LoopingConsumer consumer : consumers) {
                    consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class LoopingConsumer implements Runnable {
        private final KafkaConsumer<String, MessagePayload> consumer;
        private final List<String> topics;
        private final int id;

        public LoopingConsumer(int id,
                               String groupId,
                               List<String> topics) {
            this.id = id;
            this.topics = topics;
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            System.out.println(groupId);
            props.put("group.id", groupId);

            // this is cool - so if you set this propery (by default it is latest)
            // the consumption will start at the earliest possible message
            // so a new consumer group will start from 0 in each parition and catchup
            // after that if the consumers all go down in the group and then restart
            // the commit offset per partition will be loaded and resumed from there

            props.put("auto.offset.reset", "earliest");

            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", MessagePayloadDeserializer.class.getName());
            this.consumer = new KafkaConsumer<String,MessagePayload>(props);
        }

        @Override
        public void run() {
            try {
                consumer.subscribe(topics);

                while (true) {
                    ConsumerRecords<String, MessagePayload> records = consumer.poll(Long.MAX_VALUE);



                    for (ConsumerRecord<String, MessagePayload> record : records) {
                        Map<String, Object> data = new HashMap<>();

                        MessagePayload messagePayload = record.value();

                        processMessagePayload(messagePayload);

                        data.put("partition", record.partition());
                        data.put("offset", record.offset());
                        data.put("value", record.value());
                        System.out.println("Consumer "+this.id + ": " + data);
                    }
                }
            } catch (WakeupException e) {
                // ignore for shutdown
            } finally {
                consumer.close();
            }
        }


        private void processMessagePayload(MessagePayload messagePayload){

            System.out.println("     Processing: "+ messagePayload.getVerb() + " " + messagePayload.getObject());

        }

        public void shutdown() {
            consumer.wakeup();
        }


    }
}


