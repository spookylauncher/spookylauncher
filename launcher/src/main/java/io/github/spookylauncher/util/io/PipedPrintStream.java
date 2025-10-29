package io.github.spookylauncher.util.io;

import java.io.*;
import java.util.*;

public final class PipedPrintStream extends PrintStream {
    private final Set<PrintStream> printers = new HashSet<>();

    public static PipedPrintStream from(final PrintStream printer) {
        return new PipedPrintStream(printer);
    }
    private static OutputStream nullStream() { return new OutputStream() { public void write(int b)  {} }; }

    public PipedPrintStream(Collection<PrintStream> printers) {
        super(nullStream());

        this.printers.addAll(printers);
    }

    public void addPrinter(final PrintStream printer) {
        this.printers.add(printer);
    }

    public void removePrinter(final PrintStream printer) {
        this.printers.remove(printer);
    }

    public PipedPrintStream(PrintStream...printers) {
        super(nullStream());

        Collections.addAll(this.printers, printers);
    }

    // pls cut out my eyes

    @Override public void print(final String x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final int x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final long x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final boolean x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final double x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final float x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final Object x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final char x) { for(PrintStream printer : this.printers) printer.print(x); }
    @Override public void print(final char[] x) { for(PrintStream printer : this.printers) printer.print(x); }

    @Override public void println(final String x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final int x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final long x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final boolean x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final double x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final float x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final Object x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final char x) { for(PrintStream printer : this.printers) printer.println(x); }
    @Override public void println(final char[] x) { for(PrintStream printer : this.printers) printer.println(x); }

    @Override public void println() { for(PrintStream printer : this.printers) printer.println(); }

    @Override public PrintStream printf(final String format, final Object...args) { for(PrintStream printer : this.printers) printer.printf(format, args); return this; }
    @Override public PrintStream printf(final Locale locale, final String format, final Object...args) { for(PrintStream printer : this.printers) printer.printf(locale, format, args); return this; }
}