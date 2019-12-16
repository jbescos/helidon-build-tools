/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

package io.helidon.linker.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.helidon.linker.util.Log.Level;

import static io.helidon.linker.util.Constants.EOL;

/**
 * {@link Log.Writer} that writes to {@link System#out} and {@link System#err}.
 */
public class SystemLogWriter implements Log.Writer {
    private final int ordinal;

    /**
     * Returns a new instance with the given level.
     *
     * @param level The level at or above which messages should be logged.
     */
    public static SystemLogWriter create(Level level) {
        return new SystemLogWriter(level);
    }

    private SystemLogWriter(Level level) {
        this.ordinal = level.ordinal();
    }

    @Override
    @SuppressWarnings("checkstyle:AvoidNestedBlocks")
    public void write(Level level, Throwable thrown, String message, Object... args) {
        if (shouldWrite(level)) {
            final String msg = toString(String.format(message, args), thrown);
            switch (level) {
                case DEBUG:
                case INFO: {
                    System.out.println(msg);
                    break;
                }

                case WARN: {
                    System.err.println("WARNING: " + msg);
                    break;
                }

                case ERROR: {
                    System.err.println("ERROR: " + msg);
                    break;
                }

                default: {
                    throw new Error();
                }
            }
        }
    }

    private boolean shouldWrite(Level level) {
        return level.ordinal() >= ordinal;
    }

    private String toString(String message, Throwable thrown) {
        if (thrown == null) {
            return message;
        } else {
            StringWriter string = new StringWriter();
            PrintWriter print = new PrintWriter(string);
            thrown.printStackTrace(print);
            return message + EOL + EOL + string.toString();
        }
    }
}
