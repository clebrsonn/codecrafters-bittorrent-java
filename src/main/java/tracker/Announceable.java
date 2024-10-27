package tracker;

public interface Announceable {

    String getTrackerURL();

    byte[] getInfoHash();

    Long getInfoLength();
}
