/*
 * Copyright(C) 2010-2011 Alibaba Group Holding Limited All rights reserved. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless hasOptionValue by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.doris.cli;

/**
 * Option
 * 
 * @author Kun He (Raymond He), kun.hek@alibaba-inc.com
 * @since 1.0 2011-7-28
 */
public class Option {

    private String  shortcut;
    private String  name;
    private String  description;
    private boolean required       = true;
    private boolean hasOptionValue = true;
    private String  defaultValue;

    public Option(String shortcut, String name, String description) {
        this(shortcut, name, description, true, true);
    }

    public Option(String shortcut, String name, String description, boolean required, boolean hasOptionValue) {
        this(shortcut, name, description, required, hasOptionValue, "");
    }

    public Option(String shortcut, String name, String description, boolean required, boolean hasOptionValue,
                  String defaultValue) {
        this.shortcut = shortcut;
        this.name = name;
        this.description = description;
        this.required = required;
        this.hasOptionValue = hasOptionValue;
        this.defaultValue = defaultValue;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasOptionValue() {
        return hasOptionValue;
    }

    public void setHasOptionValue(boolean hasOptionValue) {
        this.hasOptionValue = hasOptionValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder(256);
        sbuilder.append(shortcut).append("\t");

        if (shortcut.length() <= 2) {
            sbuilder.append("\t");
        }
        sbuilder.append(name);

        if (name.length() <= 5) {
            sbuilder.append("\t");
        }

        if (required) {
            sbuilder.append("\t (required)");
        } else {
            sbuilder.append("\t");
        }

        sbuilder.append("\t").append(description).append("\r\n");

        return sbuilder.toString();
    }
}
