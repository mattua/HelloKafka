/**
 * Created by mattua on 18/11/2017.
 */
public  class MessagePayload {

    public MessagePayload(){

    }

    private String object;

    private String verb;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public MessagePayload(String object, String verb){

        this.object=object;
        this.verb=verb;
    }

    public String toString(){
        return object + " to " + verb;
    }


}