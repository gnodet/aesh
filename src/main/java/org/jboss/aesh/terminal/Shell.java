/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aesh.terminal;

import org.jboss.aesh.console.reader.AeshStandardStream;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A Shell is an abstraction of the Terminal that provides easy to use methods
 * to manipulate text/cursor/buffers.
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public interface Shell {

    /**
     * clears the screen
     */
    void clear() throws IOException;

    /**
     * Returns the {@link PrintStream} associated with the std out
     */
    PrintWriter out();

    /**
     * Returns the {@link PrintStream} associated with the std err
     */
    PrintWriter err();

    /**
     * Get the possible input stream
     */
    AeshStandardStream in();

    /**
     * @return terminal size
     */
    TerminalSize getSize();

    /**
     * @return get the cursor position
     */
    CursorPosition getCursor();

    /**
     * Set cursor position
     */
    void setCursor(CursorPosition position);

    /**
     * Move the cursor relative to the current position
     * Will not move outside of TerminalSize boundaries
     */
    void moveCursor(int rows, int columns);

    /**
     * @return true if current buffer is main
     */
    boolean isMainBuffer();

    /**
     * If not alternate buffer is enabled this will enable it
     *
     * @throws IOException
     */
    void enableAlternateBuffer();

    /**
     * If not main buffer is enabled this will enable it.
     * @throws IOException
     */
    void enableMainBuffer();

}
