package com.example.mse.powermanager.powermanager.measurements.network;


import com.example.mse.powermanager.powermanager.measurements.Measurement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NetworkMeasurement implements Measurement {
    private String interface_name;

    public NetworkMeasurement(String interface_name) {
        this.interface_name = interface_name;
    }

    public String getName() {
        return interface_name;
    }

    public static LinkedList<String> getInterfaceNames() {
        try {
            LinkedList<String> interfaceNames = new LinkedList<String>();
            Pattern p = Pattern.compile("^ *([a-zA-Z0-9]+):.*$");

            File file = new File("/proc/net/dev");
            BufferedReader net = new BufferedReader(new FileReader(file));
            String line;

            while ((line = net.readLine()) != null) {
                Matcher m = p.matcher(line);

                if (m.matches()) {
                    interfaceNames.add(m.group(1));
                }
            }

            net.close();

            return interfaceNames;
        } catch (Exception e) {
            return null;
        }
    }

    protected String[] readProcFile()
            throws Exception {
        String[] result = null;

        File file = new File("/proc/net/dev");
        BufferedReader net = new BufferedReader(new FileReader(file));
        String line;

        while ((line = net.readLine()) != null) {
            if (line.matches("^ *" + interface_name + ":.+$")) {
                result = new String[2];
                String[] segments = line.replaceAll(" {2,}", " ").split(" ");

                if (line.startsWith(" ")) {
                    result[0] = segments[2];
                    result[1] = segments[10];
                } else {
                    result[0] = segments[1];
                    result[1] = segments[9];
                }

                break;
            }
        }

        net.close();
        return result;
    }
}

