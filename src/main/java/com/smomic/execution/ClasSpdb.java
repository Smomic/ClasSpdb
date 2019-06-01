package com.smomic.execution;

import org.apache.commons.cli.*;
import java.io.IOException;

import static com.smomic.data.Parameters.*;

public class ClasSpdb {

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        Application application = new Application();

        try {
            cmd = parser.parse(Application.generateOptions(), args);

            if (cmd.hasOption(HELP.getValue()))
                Application.help();

            if (cmd.hasOption(CLASS_TYPE.getValue())) {
                application.executeController(cmd);
                System.exit(0);
            } else {
                Application.help();
            }
        } catch (ParseException e) {
            Application.help();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}