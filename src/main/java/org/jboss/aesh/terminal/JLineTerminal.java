package org.jboss.aesh.terminal;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.jboss.aesh.console.reader.AeshStandardStream;
import org.jboss.aesh.console.settings.Settings;
import org.jline.Console;
import org.jline.JLine;
import org.jline.console.Attributes;
import org.jline.console.Size;
import org.jline.utils.InfoCmp.Capability;

/**
 * Created by gnodet on 07/10/15.
 */
public class JLineTerminal implements Shell, Terminal {

    private final org.jline.Console console;
    private boolean mainBuffer;
    private Attributes attributes;

    public JLineTerminal(Console console) {
        this.console = console;
        mainBuffer = true;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void clear() throws IOException {
        console.puts(Capability.clear_screen);
    }

    @Override
    public PrintWriter out() {
        return console.writer();
    }

    @Override
    public PrintWriter err() {
        return null;
    }

    @Override
    public AeshStandardStream in() {
        return null;
    }

    @Override
    public TerminalSize getSize() {
        Size size = console.getSize();
        return new TerminalSize(size.getRows(), size.getColumns());
    }

    @Override
    public CursorPosition getCursor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursor(CursorPosition position) {
        if(getSize().isPositionWithinSize(position)) {
            console.puts(Capability.cursor_address, position.getRow(), position.getColumn());
            console.writer().flush();
        }
    }

    @Override
    public void moveCursor(int rows, int columns) {
        CursorPosition cp = getCursor();
        cp.move(rows, columns);
        if(getSize().isPositionWithinSize(cp)) {
            setCursor(cp);
        }
    }

    @Override
    public boolean isMainBuffer() {
        return mainBuffer;
    }

    @Override
    public void enableAlternateBuffer() {
        console.puts(Capability.enter_ca_mode);
        mainBuffer = false;
    }

    @Override
    public void enableMainBuffer() {
        console.puts(Capability.exit_ca_mode);
        mainBuffer = true;
    }

    @Override
    public void init(Settings settings) {
        attributes = console.enterRawMode();
    }

    @Override
    public int[] read() throws IOException {
        int c = console.reader().read();
        return new int[] { c };
    }

    @Override
    public boolean hasInput() {
        try {
            return console.reader().ready();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public boolean isEchoEnabled() {
        return console.echo();
    }

    @Override
    public void reset() throws IOException {
        console.setAttributes(attributes);
    }

    @Override
    public Shell getShell() {
        return this;
    }

    @Override
    public void writeToInputStream(String data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changeOutputStream(PrintStream output) {
        throw new UnsupportedOperationException();
    }
}
