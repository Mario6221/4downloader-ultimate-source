package pl.czyz;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


public final class ThreadDownloader {
    final static private ThreadDownloader instance = new ThreadDownloader();
    final private AtomicInteger counter = new AtomicInteger(0);
    final private Object lock1 = new Object();

    public synchronized AtomicInteger getCounter() {
        return counter;
    }

    public synchronized Object getLock() {
        return lock1;
    }

    private ThreadDownloader(){

    }

    public static ThreadDownloader getInstance(){
        return instance;
    }

    public final void update(long timeout){
        File[] files = new File("Downloads//.temp").listFiles();
        String filename[];
        if (files != null || timeout<15) {
            assert files != null;
            while (files.length!=0){
                for(File file: files){
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    filename = file.getName().split(Pattern.quote("!@#"));
                    AttributesAndUtils.getInstance().setBoard(filename[0]);
                    AttributesAndUtils.getInstance().setThreadnumber(filename[1].split(Pattern.quote("."))[0]);
                    updateThread();
                }
                for(int i=0;i<timeout;i++){
                    try {
                        if (!AttributesAndUtils.getInstance().isSilent())
                        System.out.print("\rWaiting for "+timeout+" minutes ["+i+"/"+timeout+"]");
                        TimeUnit.MINUTES.sleep(1L);
                    } catch (InterruptedException e) {
                        if (!AttributesAndUtils.getInstance().isSilent())
                        e.printStackTrace();
                    }
                }
                if (!AttributesAndUtils.getInstance().isSilent())
                    System.out.println();
                files = new File("Downloads//.temp").listFiles();
                if (files == null){
                    break;
                }
            }
        }
    }

    void downloadThread(String board,String threadnumber){
        AttributesAndUtils.getInstance().setBoard(board);
        AttributesAndUtils.getInstance().setThreadnumber(threadnumber);
        try {
            JSONArray arr = new JSONObject(Objects.requireNonNull(downloadJSON(new URL("http://a.4cdn.org/" + board + "/thread/" + threadnumber + ".json"), new File("Downloads//.temp//" + board + "!@#" + threadnumber + ".json")))).getJSONArray("posts");
            if (!AttributesAndUtils.getInstance().isDestinationFlag())
                AttributesAndUtils.getInstance().setDestination(AttributesAndUtils.getInstance().getBoard() + "//[" + arr.getJSONObject(0).getString("semantic_url") + "]" + AttributesAndUtils.getInstance().getThreadnumber());
            downloadImages(arr,readOld(new File("Downloads//new//"+AttributesAndUtils.getInstance().getDestination())));
        } catch (MalformedURLException e) {
            if (!AttributesAndUtils.getInstance().isSilent())
            e.printStackTrace();
        }
        try {
            if (!AttributesAndUtils.getInstance().isCopyFlag())
            Files.delete(FileSystems.getDefault().getPath("Downloads//.temp//"+board+"!@#"+threadnumber+".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String downloadJSON(URL url,File file) {
        try {
            new File("Downloads//.temp").mkdirs();
            FileUtils.copyURLToFile(url,file,5000,5000);
            return AttributesAndUtils.getInstance().readFile(file.getPath(),Charset.defaultCharset());
        } catch (IOException e) {
            if(!AttributesAndUtils.getInstance().isSilent()) {
                e.printStackTrace();
                System.out.println("Thread URL is incorrect / Thread does not exist");
            }
            System.exit(0);
        }
        return null;
    }

    private void downloadImages(JSONArray arr,int oldx) {
        int old = oldx;
        int num = arr.getJSONObject(0).getInt("images")+1;
        if(old!=num-1) {
            if (!AttributesAndUtils.getInstance().isDestinationFlag())
                AttributesAndUtils.getInstance().setDestination(AttributesAndUtils.getInstance().getBoard() + "//[" + arr.getJSONObject(0).getString("semantic_url") + "]" + AttributesAndUtils.getInstance().getThreadnumber());
            new File("Downloads//new//" + AttributesAndUtils.getInstance().getDestination()).mkdirs();
            if (AttributesAndUtils.getInstance().isCopyFlag()) {
                new File("Downloads//permanent//" + AttributesAndUtils.getInstance().getDestination()).mkdirs();
            }
            counter.set(old);
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).has("ext")) {
                    if (old != 0) {
                        old--;
                    } else {
                        new Thread(new SimpleDownloader(String.valueOf(arr.getJSONObject(i).getLong("tim")), arr.getJSONObject(i).getString("ext"))).start();
                    }
                }
            }
            checkAndWait(num, oldx, arr.getJSONObject(0).getString("semantic_url"));

            if (AttributesAndUtils.getInstance().isCopyFlag())
                copy();
        }
    }

    private void copy(){
        try {
            Thread.sleep(2000);
            FileUtils.copyDirectory(new File("Downloads//new//"+AttributesAndUtils.getInstance().getDestination()),new File("Downloads//permanent//"+AttributesAndUtils.getInstance().getDestination()));
        } catch (IOException | InterruptedException e) {
            if (!AttributesAndUtils.getInstance().isSilent())
            e.printStackTrace();
        }
    }

    private void checkAndWait(int num,int old,String semUrl){
        int temp=counter.get();
        while(temp!=num){
            if(!AttributesAndUtils.getInstance().isSilent())
            System.out.print("\rDownloading "+semUrl+"["+(temp-old)+"/"+(num-old)+"]");
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                if (!AttributesAndUtils.getInstance().isSilent())
                e.printStackTrace();
            }
            temp=counter.get();
        }
        if (!AttributesAndUtils.getInstance().isSilent())
        System.out.print("\rThread ["+semUrl+"] downloaded successfully\n");
    }

    private boolean updateThread(){
        int oldImgs = readOld(new File("Downloads//new//"+AttributesAndUtils.getInstance().getDestination()));
        if (oldImgs==0)
            return false;
        else {
            JSONArray arr = null;
            try {
                arr = new JSONObject(Objects.requireNonNull(downloadJSON(new URL("http://a.4cdn.org/" + AttributesAndUtils.getInstance().getBoard() + "/thread/" + AttributesAndUtils.getInstance().getThreadnumber() + ".json"), new File("Downloads//.temp//" + AttributesAndUtils.getInstance().getBoard() + "!@#" + AttributesAndUtils.getInstance().getThreadnumber() + ".json")))).getJSONArray("posts");
            } catch (MalformedURLException e) {
                if (!AttributesAndUtils.getInstance().isSilent())
                    e.printStackTrace();
            }
            JSONObject obj = arr.getJSONObject(0);
            if(obj.getInt("bumplimit")==1||obj.getInt("imagelimit")==1||obj.has("archived")||obj.has("closed")) {
                try {
                    Files.delete(FileSystems.getDefault().getPath("Downloads//.temp//"+AttributesAndUtils.getInstance().getBoard()+"!@#"+AttributesAndUtils.getInstance().getThreadnumber()+".json"));
                } catch (IOException e) {
                    if (!AttributesAndUtils.getInstance().isSilent())
                    e.printStackTrace();
                }
            }
            downloadImages(arr,oldImgs);
            return true;
        }
    }

    private int readOld(File f){
        if(f.isDirectory()){
            return f.listFiles().length;
        }
        else {
            return 0;
        }
    }

    public List<String> downloadThreadList(String b,String regex){
        new File("Downloads//.temp2").mkdirs();
        List<String> result = new ArrayList<>();
        File file = new File("Downloads//.temp2//" + b + "catalog.json");
        JSONArray allPages = null;
        try {
            allPages = new JSONArray(Objects.requireNonNull(downloadJSON(new URL("http://a.4cdn.org/" + b + "/catalog.json"), file)));
        } catch (MalformedURLException e) {
            if (!AttributesAndUtils.getInstance().isSilent())
                e.printStackTrace();
        }
        assert allPages != null;
        for(int i = 0; i<allPages.length(); i++){
           JSONArray temp = allPages.getJSONObject(i).getJSONArray("threads");
           for (int j=0;j<temp.length();j++){
               JSONObject tempobj = temp.getJSONObject(j);
               if(tempobj.has("sub")){
                   if (tempobj.getString("sub").matches(regex))
                       result.add("http://boards.4chan.org/"+b+"/thread/"+tempobj.getInt("no"));
               }
           }
        }
        try {
            Files.delete(FileSystems.getDefault().getPath(file.getPath()));
        } catch (IOException e) {
            if (!AttributesAndUtils.getInstance().isSilent())
                 e.printStackTrace();
        }
        return result;
    }

}

//   http://boards.4chan.org/g/thread/1234213

//   http://i.4cdn.org/board/tim.ext

// http://a.4cdn.org/board/catalog.json