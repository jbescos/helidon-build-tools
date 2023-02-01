/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
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

package io.helidon.build.maven.enforcer.inclusivenaming;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Copyright configuration to be used with Maven plugin.
 *
 * @see io.helidon.build.maven.enforcer.EnforcerMojo
 */
public class InlusiveNamingConfig {
    /**
     * Fail if copyright is invalid.
     */
    @Parameter(defaultValue = "false")
    private boolean failOnError;

    /**
     * List of suffixes (such as {@code .js}) and file names (such as {@code Dockerfile}) to include.
     */
    @Parameter
    private String[] includes;

    /**
     * List of suffixes (such as {@code .js}) and file names (such as {@code Dockerfile}) to exclude.
     */
    @Parameter
    private String[] excludes;

    /**
     * Regular expression containing to exclude terms (such as {@code ((?i)master)|((?i)slave)}).
     */
    @Parameter
    private String excludeTermsRegExp;

    /**
     * Inclusions of terms.
     */
    @Parameter
    private XmlData[] includeTerms;

    /**
     * File with the inclusive naming JSON {@link https://inclusivenaming.org/word-lists/index.json}.
     */
    @Parameter
    private File inclusiveNamingFile;

    @Override
    public String toString() {
        return "InlusiveNamingConfig{"
                + "failOnError=" + failOnError
                + ", inclusiveNamingFile=" + inclusiveNamingFile
                + ", includes=" + Arrays.toString(includes)
                + ", excludes=" + Arrays.toString(excludes)
                + ", excludeTermsRegExp=" + excludeTermsRegExp
                + '}';
    }

    /**
     * Whether this rule is configured to fail on error.
     *
     * @return whether to fail on error
     */
    public boolean failOnError() {
        return failOnError;
    }

    Set<String> excludes() {
        if (excludes == null) {
            return Set.of();
        }
        return Set.of(excludes);
    }

    Set<String> includes() {
        if (includes == null) {
            return Set.of();
        }
        return Set.of(includes);
    }

    Optional<Pattern> excludeTermsRegExp() {
        Pattern pattern = null;
        if (excludeTermsRegExp != null) {
            pattern  = Pattern.compile(excludeTermsRegExp);
        }
        return Optional.ofNullable(pattern);
    }

    Optional<File> inclusiveNamingFile() {
        return Optional.ofNullable(inclusiveNamingFile);
    }

    List<XmlData> includeTerms() {
        if (includeTerms == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(includeTerms);
    }
}
