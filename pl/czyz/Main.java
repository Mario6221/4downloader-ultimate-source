package pl.czyz;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        Option help = new Option("h","help",false,"get this helper message");
        help.setRequired(false);
        options.addOption(help);

        Option fromFile = new Option("f","file",true,"gets the urls from file, structure:\n<link>\n<link>\n...");
        fromFile.setRequired(false);
        options.addOption(fromFile);

        Option update = new Option("u","update",true,"updates all missing images within all unarchived threads that you downloaded before. Argument is the timeout in minutes. Cant be lower than 15");
        update.setRequired(false);
        options.addOption(update);

        Option less = new Option("s","silent",false,"runs in silent mode");
        less.setRequired(false);
        options.addOption(less);

        Option destination = new Option("d","destination",true,"sets the download destination, otherwise set to default");
        destination.setRequired(false);
        options.addOption(destination);

        Option copy = new Option("c","copy",false,"does not copy the contents to \"permanent\" directory");
        copy.setRequired(false);
        options.addOption(copy);

        Option regex = new Option("r","regex",true,"the regex to search for in threads in catalog of the board specified in option -b");
        regex.setRequired(false);
        options.addOption(regex);

        Option boards = new Option("b","board",true,"the board to search through with regex specified in option -r. Works only with -r");
        regex.setRequired(false);
        options.addOption(boards);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter f = new HelpFormatter();
        boolean lessx=false;
        boolean copyFlag=true;
        ThreadDownloader x;
        try {
            CommandLine line = parser.parse(options,args,true);
            if(line.hasOption("help")){
                f.printHelp("4downloader-ultimate [-h] [-s] [-d] [-c] [-u] [-f] [-r] [-b]",options);
                System.exit(0);
            }
            if(line.hasOption("silent"))
                lessx=true;
            if(line.hasOption("copy")){
                copyFlag=false;
            }
            if(line.hasOption("destination"))
                AttributesAndUtils.getInstance().setDestination(line.getOptionValue("d"));

                AttributesAndUtils.getInstance().setFlags(lessx,copyFlag);

            List<String> urls = line.getArgList();
            if(line.hasOption("file")){
                try {
                    String argFile[] = AttributesAndUtils.getInstance().readFile(line.getOptionValue("f"),Charset.defaultCharset()).split(Pattern.quote("\n"));
                    urls.addAll(Arrays.asList(argFile));
                } catch (IOException e) {
                    if (!AttributesAndUtils.getInstance().isSilent())
                        System.out.println(e.getMessage());
                }
            }

            if (line.hasOption("regex")&&line.hasOption("board")){
                urls.addAll(ThreadDownloader.getInstance().downloadThreadList(line.getOptionValue("board"),line.getOptionValue("regex")));
            }

            for(String url: urls){
                String thread[] = url.split(Pattern.quote("//"))[1].split(Pattern.quote("/"));
                String board = thread[1];
                String threadnumber = thread[3];
                ThreadDownloader.getInstance().downloadThread(board,threadnumber);
            }

            if (line.hasOption("update")){
                ThreadDownloader.getInstance().update(Long.parseLong(line.getOptionValue("update")));
            }

        }
        catch (ParseException e){
            f.printHelp("4downloader-ultimate [-h] [-s] [-d] [-c] [-u] [-f] [-r] [-b]",options);
        }
    }
}
