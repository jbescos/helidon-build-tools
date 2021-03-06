/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
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

package io.helidon.build.util;

import java.io.IOException;
import java.util.Optional;

import org.fusesource.jansi.Ansi;
import org.junit.jupiter.api.Test;

import static io.helidon.build.util.Requirements.failed;
import static io.helidon.build.util.Style.BoldRed;
import static io.helidon.build.util.Style.Cyan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for class {@link Requirements}.
 */
class RequirementsTest {
    static {
        System.setProperty("jansi.force", "true");
        AnsiConsoleInstaller.ensureInstalled();
    }

    @Test
    void testDoesNotConvert() {
        Throwable throwable = new Error();
        Optional<RequirementFailure> conversion = Requirements.toFailure(throwable);
        assertThat(conversion, is(not(nullValue())));
        assertThat(conversion.isPresent(), is(false));
    }

    @Test
    void testNestedDoesNotConvert() {
        Throwable throwable = new RuntimeException(new IOException(new Error()));
        Optional<RequirementFailure> conversion = Requirements.toFailure(throwable);
        assertThat(conversion, is(not(nullValue())));
        assertThat(conversion.isPresent(), is(false));
    }

    @Test
    void testConverts() {
        Throwable throwable = new RequirementFailure("fail");
        Optional<RequirementFailure> conversion = Requirements.toFailure(throwable);
        assertThat(conversion, is(not(nullValue())));
        assertThat(conversion.isPresent(), is(true));
        assertThat(conversion.get(), is(sameInstance(throwable)));
    }

    @Test
    void testNestedConverts() {
        Throwable nested = new RequirementFailure("fail");
        Throwable throwable = new RuntimeException(new Error(new IOException(nested)));
        Optional<RequirementFailure> conversion = Requirements.toFailure(throwable);
        assertThat(conversion, is(not(nullValue())));
        assertThat(conversion.isPresent(), is(true));
        assertThat(conversion.get(), is(sameInstance(nested)));
    }

    @Test
    void testAnsiColors() {
        Ansi.setEnabled(true);
        String expected = "This is " + BoldRed.apply("bold red") + " text, and this is " + Cyan.apply("cyan") + " text.";
        try {
            failed("This is $(RED %s) text, and this is $(cyan %s) text.", "bold red", "cyan");
            fail("Should have failed!");
        } catch (RequirementFailure e) {
            assertThat(e.getMessage(), is(expected));
            System.out.println("Got expected message: " + e.getMessage());
        }
    }
}
