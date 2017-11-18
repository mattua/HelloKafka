import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class MessagePayloadDeserializer implements Deserializer<MessagePayload> {

    @Override public void close() {

    }

    @Override public void configure(Map<String, ?> arg0, boolean arg1) {

    }

    @Override
    public MessagePayload deserialize(String arg0, byte[] arg1) {

        ObjectMapper mapper = new ObjectMapper();
        MessagePayload messagePayload = null;
        try {



            messagePayload = mapper.readValue(arg1,MessagePayload.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return messagePayload;
    }

}