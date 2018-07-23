package pl.czyz;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class AttributesAndUtils {
    private static AttributesAndUtils instance = new AttributesAndUtils();
    private boolean less;
    private boolean destinationFlag = false;
    private boolean copyFlag;
    private String destination="";
    private String board;
    private String threadnumber;


    private AttributesAndUtils(){

    }

    public static AttributesAndUtils getInstance() {
        return instance;
    }

    public void setFlags(boolean less,boolean copyFlag){
        this.less = less;
        this.copyFlag=copyFlag;
    }

    public final boolean isSilent() {
        return less;
    }

    public final boolean isDestinationFlag() {
        return destinationFlag;
    }

    public final boolean isCopyFlag() {
        return copyFlag;
    }

    public final String getDestination() {
        return destination;
    }

    public final String getBoard() {
        return board;
    }

    public final String getThreadnumber() {
        return threadnumber;
    }

    public final void setDestination(String destination) {
        this.destination = destination;
        this.destinationFlag=true;
    }

    public final void setBoard(String board) {
        this.board = board;
    }

    public final void setThreadnumber(String threadnumber) {
        this.threadnumber = threadnumber;
    }

    public final String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
