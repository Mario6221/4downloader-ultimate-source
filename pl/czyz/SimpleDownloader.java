package pl.czyz;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class SimpleDownloader implements Runnable {
    private String tim;
    private String ext;

    SimpleDownloader(String tim, String ext) {
        this.tim=tim;
        this.ext=ext;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://i.4cdn.org/" + AttributesAndUtils.getInstance().getBoard() + "/" + tim + ext);
            FileUtils.copyURLToFile(url,new File("Downloads//new//"+AttributesAndUtils.getInstance().getDestination()+"//"+tim + ext),10000,10000);
            synchronized (ThreadDownloader.getInstance().getLock()){
                ThreadDownloader.getInstance().getCounter().getAndIncrement();
            }
        } catch (Exception e) {
            if (!AttributesAndUtils.getInstance().isSilent())
            System.out.println("\nError on " + tim);
            synchronized (ThreadDownloader.getInstance().getLock()){
                ThreadDownloader.getInstance().getCounter().getAndIncrement();
            }
        }
    }
}
// http://i.4cdn.org/board/tim.ext

// destination+"//"+String.valueOf(obj.getLong("tim")) + obj.getString("ext")
