/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.jreadline.cl.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ParameterInt {

    private String usage;
    private List<OptionInt> options;

    public ParameterInt(String usage) {
        setUsage(usage);
        setOptions(new ArrayList<OptionInt>());
    }

    public ParameterInt(String usage, OptionInt[] options) {
        setUsage(usage);
        setOptions(Arrays.asList(options));
    }

    public List<OptionInt> getOptions() {
        return options;
    }

    public void addOption(char name, String longName, String description, boolean hasValue,
                     String argument, boolean required, Object type) {
        options.add(new OptionInt(name, longName, description,
                hasValue, argument, required, '\u0000', false, type));
    }

    private void setOptions(List<OptionInt> options) {
        this.options = options;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public OptionInt findOption(String name) {
        for(OptionInt option : options)
            if(option.getName().equals(name))
                return option;

        return null;
    }

    public OptionInt findLongOption(String name) {
        for(OptionInt option : options)
            if(option.getLongName().equals(name))
                return option;

        return null;
    }

    public OptionInt startWithLongOption(String name) {
        for(OptionInt option : options)
            if(option.getLongName().startsWith(name))
                return option;

        return null;
    }

}