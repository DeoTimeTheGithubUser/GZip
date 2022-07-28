package me.deotime.gzip;

import java.io.*;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Main {

    public static void main(String[] args) {
        File file = null;
        Mode mode = null;

        if(args.length < 1) err("No mode specified. Possible modes: -zip, -unzip.");
        else if ((mode = Mode.fromArg(args[0])) == null) err("Invalid mode. Possible modes: -zip, -unzip.");
        else if (!(file = new File(String.join(" ", Arrays.copyOfRange(args, 1, args.length)))).exists()) err("Could not find file \"" + file.getName() + "\".");

        try {
            if (mode == Mode.ZIP) zipFile(file);
            else unzipFile(file);
        } catch (Exception ex) {
            err("Unable to " + (mode == Mode.UNZIP ? "un" : "") + "zip file.");
        }
    }

    private static void zipFile(File file) throws Exception {
        byte[] zipped = zip(read(file));
        File gFile = new File(file.getName() + ".gz");
        gFile.createNewFile();
        write(gFile, new String(zipped));
        System.out.println("GZipped file.");
    }

    private static void unzipFile(File file) throws Exception {
        if(!file.getName().endsWith(".gz")) err("Not a GZipped file.");
        byte[] unzipped = unzip(read(file));
        File unzippedFile = new File(file.getName().substring(0, file.getName().length() - 3));
        write(unzippedFile, new String(unzipped));
        System.out.println("Un-GZipped File.");
    }

    private static byte[] zip(byte[] arr) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gOut = new GZIPOutputStream(out);
        gOut.write(arr);
        gOut.flush();
        gOut.close();
        return out.toByteArray();
    }

    private static byte[] unzip(byte[] arr) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(arr);
        GZIPInputStream gIn = new GZIPInputStream(in);
        return gIn.readAllBytes();
    }

    private static byte[] read(File file) throws Exception {
        return String.join("\n", new BufferedReader(new FileReader(file)).lines().toArray(String[]::new)).getBytes();
    }

    private static void write(File file, String str) throws Exception {
        FileWriter writer = new FileWriter(file);
        writer.write(str);
        writer.flush();
        writer.close();
    }

    private static void err(String msg) {
        System.out.println(msg);
        System.exit(1);
    }

    private enum Mode {
        ZIP,
        UNZIP;

        static Mode fromArg(String arg) {
            return switch (arg.substring(1).toLowerCase()) {
                case "zip", "z" -> ZIP;
                case "unzip", "u" -> UNZIP;
                default -> null;
            };
        }
    }

}
