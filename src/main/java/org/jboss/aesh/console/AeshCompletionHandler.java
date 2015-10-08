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
package org.jboss.aesh.console;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.alias.Alias;
import org.jboss.aesh.console.alias.AliasManager;
import org.jboss.aesh.console.operator.ControlOperatorParser;
import org.jboss.aesh.console.operator.RedirectionCompletion;
import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.aesh.util.LoggerUtil;
import org.jline.Candidate;
import org.jline.Completer;
import org.jline.ConsoleReader;
import org.jline.reader.ParsedLine;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class AeshCompletionHandler implements Completer {

    private volatile boolean enabled = true;

    private final AeshContext aeshContext;
    private final List<Completion> completionList;
    private AliasManager aliasManager;
    private final boolean doLogging;

    private static final Logger LOGGER = LoggerUtil.getLogger(AeshCompletionHandler.class.getName());

    public AeshCompletionHandler(AeshContext aeshContext, boolean doLogging) {
        completionList = new ArrayList<>();
        this.aeshContext = aeshContext;
        this.doLogging = doLogging;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public void addCompletion(Completion completion) {
        completionList.add(completion);
    }

    public void removeCompletion(Completion completion) {
        completionList.remove(completion);
    }

    public void setAliasManager(AliasManager aliasManager) {
        this.aliasManager = aliasManager;
    }

    @Override
    public void complete(ConsoleReader reader, ParsedLine line, List<Candidate> candidates) {
        if(!enabled)
            return;

        if(completionList.size() < 1)
            return;

        String buffer = line.line();

        List<CompleteOperation> possibleCompletions = new ArrayList<>();
        int pipeLinePos = 0;
        boolean redirect = false;
        if(ControlOperatorParser.doStringContainPipelineOrEnd(buffer)) {
            pipeLinePos =  ControlOperatorParser.findLastPipelineAndEndPositionBeforeCursor(line.line(), line.cursor());
        }
        if(ControlOperatorParser.findLastRedirectionPositionBeforeCursor(line.line(), line.cursor()) > pipeLinePos) {
            pipeLinePos = 0;
            redirect = true;
        }

        for(int i=0; i < completionList.size(); i++) {
            if(redirect && !completionList.get(i).getClass().equals(RedirectionCompletion.class)) {
                break;
            }
            CompleteOperation co;
            if(pipeLinePos > 0) {
                co = findAliases(line.line().substring(pipeLinePos, line.cursor()), line.cursor() - pipeLinePos);
            }
            else {
                co = findAliases(line.line(), line.cursor());
            }

            completionList.get(i).complete(co);

            if(co.getCompletionCandidates() != null && co.getCompletionCandidates().size() > 0)
                possibleCompletions.add(co);
        }

        if(doLogging)
            LOGGER.info("Found completions: "+possibleCompletions);

        for (CompleteOperation co : possibleCompletions) {
            for (TerminalString ts : co.getCompletionCandidates()) {
                String disp = ts.toString();
                String val = ts.getCharacters();
                candidates.add(new Candidate(val, disp, null, null, null, null, true));
            }
        }

    }

    private CompleteOperation findAliases(String buffer, int cursor) {
        if(aliasManager != null) {
            String command = Parser.findFirstWord(buffer);
            Alias alias = aliasManager.getAlias(command);
            if(alias != null) {
                return new CompleteOperation(aeshContext, alias.getValue()+buffer.substring(command.length()),
                        cursor+(alias.getValue().length()-command.length()));
            }
        }

        return new CompleteOperation(aeshContext, buffer, cursor);
    }
}
