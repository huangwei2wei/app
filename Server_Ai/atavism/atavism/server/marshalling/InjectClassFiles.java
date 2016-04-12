// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.marshalling;

import java.util.Hashtable;
import java.util.List;
import java.io.File;
import java.util.LinkedList;
import atavism.server.util.Log;
import java.util.Properties;

public class InjectClassFiles
{
    public static void main(final String[] argv) throws Throwable {
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.File", "${atavism.logs}/inject.out");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.MaxFileSize", "50MB");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        ((Hashtable<String, String>)props).put("log4j.appender.FILE.layout.ConversionPattern", "%-5p %m%n");
        ((Hashtable<String, String>)props).put("atavism.log_level", "0");
        ((Hashtable<String, String>)props).put("log4j.rootLogger", "DEBUG, FILE");
        Log.init(props);
        if (argv.length < 6 || (argv.length & 0x1) == 0x1) {
            usage();
            System.exit(1);
        }
        final List<String> marshallersFiles = new LinkedList<String>();
        String inputDir = "";
        String outputDir = "";
        String typeNumFileName = "";
        for (int i = 0; i < argv.length; i += 2) {
            final String arg = argv[i];
            final String value = argv[i + 1];
            if (arg.equals("-m")) {
                final File marshallersFile = new File(value);
                if (marshallersFile.isFile()) {
                    marshallersFiles.add(value);
                }
            }
            else if (arg.equals("-i")) {
                final File in = new File(value);
                if (in.isDirectory()) {
                    inputDir = value;
                }
                else {
                    System.err.println("Class file input directory '" + value + "' does not exist!");
                    System.exit(1);
                }
            }
            else if (arg.equals("-o")) {
                final File out = new File(value);
                if (out.isDirectory()) {
                    out.mkdir();
                }
                outputDir = value;
            }
            else if (arg.equals("-t")) {
                typeNumFileName = value;
            }
        }
        if (marshallersFiles.size() == 0) {
            System.err.println("No marshaller files were supplied!");
            System.exit(1);
        }
        if (inputDir == "") {
            System.err.println("The class file input directory was not supplied!");
            System.exit(1);
        }
        if (outputDir == "") {
            System.err.println("The class file output directory was not supplied!");
            System.exit(1);
        }
        if (typeNumFileName == "") {
            System.err.println("The typenumbers.txt file name to which type numbers are written was not supplied!");
            System.exit(1);
        }
        final String[] mr_argv = new String[argv.length + 1];
        System.arraycopy(argv, 0, mr_argv, 0, argv.length);
        mr_argv[argv.length] = "-r";
        if (MarshallingRuntime.initialize(mr_argv)) {
            System.out.println("Exiting because MarshallingRuntime.initialize() found missing or incorrect classes");
            System.exit(1);
        }
    }
    
    protected static void usage() {
        System.out.println("Usage: java atavism.server.marshalling.InjectClassFiles [ -m marshallersfile.txt | -i input_directory | -o output_directory ]");
    }
}
