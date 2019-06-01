package com.smomic.execution;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;

import static com.smomic.data.Parameters.*;
import static com.smomic.data.Parameters.CLASS_TYPE;

class Application {
    private static Options options;

    Application() {
        options = generateOptions();
    }

    void executeController(CommandLine cmd) throws IOException {
        String classType = cmd.getOptionValue(CLASS_TYPE.getValue());
        boolean generate = cmd.hasOption(GENERATE.getValue());

        if (cmd.hasOption(FILE_PATH.getValue()) &&
                cmd.hasOption(NUM_OF_TESTS.getValue()) &&
                cmd.hasOption(THRESHOLD.getValue())) {
            new Controller(classType, generate, cmd.getOptionValue(FILE_PATH.getValue()),
                    Integer.valueOf(cmd.getOptionValue(NUM_OF_TESTS.getValue())),
                    Integer.valueOf(cmd.getOptionValue(THRESHOLD.getValue()))).run();
        } else if (cmd.hasOption(FILE_PATH.getValue()) && cmd.hasOption(NUM_OF_TESTS.getValue())) {
            new Controller(classType, generate, cmd.getOptionValue(FILE_PATH.getValue()),
                    Integer.valueOf(cmd.getOptionValue(NUM_OF_TESTS.getValue()))).run();
        } else if (cmd.hasOption(THRESHOLD.getValue()) && cmd.hasOption(NUM_OF_TESTS.getValue())) {
            new Controller(classType, generate,
                    Integer.valueOf(cmd.getOptionValue(NUM_OF_TESTS.getValue())),
                    Integer.valueOf(cmd.getOptionValue(THRESHOLD.getValue()))).run();
        } else if (cmd.hasOption(FILE_PATH.getValue()) && cmd.hasOption(THRESHOLD.getValue())) {
            new Controller(classType, generate, cmd.getOptionValue(FILE_PATH.getValue()),
                    -1, Integer.valueOf(cmd.getOptionValue(THRESHOLD.getValue()))).run();
        } else if (cmd.hasOption(THRESHOLD.getValue())) {
            new Controller(classType, generate, -1,
                    Integer.valueOf(cmd.getOptionValue(THRESHOLD.getValue()))).run();
        } else if (cmd.hasOption(FILE_PATH.getValue())) {
            new Controller(classType, generate, cmd.getOptionValue(FILE_PATH.getValue())).run();
        } else if (cmd.hasOption(NUM_OF_TESTS.getValue())) {
            new Controller(classType, generate, Integer.valueOf(cmd.getOptionValue(NUM_OF_TESTS.getValue()))).run();
        } else {
            new Controller(classType, generate).run();
        }
    }

    static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(APP_NAME.getValue(), options);
        System.exit(0);
    }

    static Options generateOptions() {
        final Option classificationOption = Option.builder(CLASS_TYPE.getValue())
                .required()
                .hasArg()
                .desc("Classification type (e.g transit_private)")
                .build();
        final Option dataOption = Option.builder("g")
                .required(false)
                .desc("Specify, if generate data. Not setting use data from existing file.")
                .build();
        final Option fileOption = Option.builder("f")
                .required(false)
                .hasArg()
                .desc("File with generated data or which data will be saved, DEFAULT the same name as classification type.")
                .build();
        final Option numberOption = Option.builder("n")
                .required(false)
                .hasArg()
                .desc("Number of tests, DEFAULT is 1")
                .build();
        final Option thresholdOption = Option.builder("t")
                .required(false)
                .hasArg()
                .desc("Threshold in RelieF algorithm, DEFAULT is 200")
                .build();
        final Options options = new Options();
        options.addOption(classificationOption);
        options.addOption(dataOption);
        options.addOption(fileOption);
        options.addOption(numberOption);
        options.addOption(thresholdOption);
        return options;
    }
}
