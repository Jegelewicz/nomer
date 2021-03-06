package org.globalbioticinteractions.nomer.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CmdLine {
    private final static Log LOG = LogFactory.getLog(CmdLine.class);

    public static void run(JCommander actual) {
        if (actual == null) {
            throw new MissingCommandException("no command provided");
        } else if (!(actual.getObjects().get(0) instanceof Runnable)) {
            throw new MissingCommandException("invalid command provided");
        } else {
            Object cmdObject = actual.getObjects().get(0);
            ((Runnable) cmdObject).run();
        }
    }

    public static void run(String[] args) throws Throwable {
        JCommander jc = new CmdLine().buildCommander();
        jc.setProgramName("nomer");
        try {
            jc.parse(args);
            CmdLine.run(jc.getCommands().get(jc.getParsedCommand()));
        } catch (Throwable ex) {
            LOG.error("unexpected exception", ex);
            StringBuilder out = new StringBuilder();
            jc.usage(out);
            System.err.append(out.toString());
            throw ex;
        }
    }

    public class CommandMain implements Runnable {

        @Override
        public void run() {

        }
    }

    JCommander buildCommander() {
        return JCommander.newBuilder()
                .addObject(new CommandMain())
                .addCommand("version", new CmdVersion())
                .addCommand("replace", new CmdReplace())
                .addCommand("append", new CmdAppend())
                .addCommand("matchers", new CmdMatchers())
                .addCommand("properties", new CmdProperties())
                .addCommand("input-schema", new CmdInputSchema())
                .addCommand("output-schema", new CmdOutputSchema())
                .addCommand("validate-term", new CmdValidateTerms())
                .addCommand("validate-term-link", new CmdValidateLinks())
                .addCommand("clean", new CmdClean())
                .build();
    }


}