/*
 * Copyright (c) 2020, 2020 Oracle and/or its affiliates.
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

package io.helidon.linker;

import org.junit.jupiter.api.Test;

import static io.helidon.linker.Application.versionFromFileName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for class {@link Application}.
 */
class ApplicationTest {

    @Test
    void testVersionParsing() {
        assertThat(versionFromFileName("helidon-config-1.4.2.jar"), is("1.4.2"));
        assertThat(versionFromFileName("helidon-config-1.4.3-SNAPSHOT.jar"), is("1.4.3-SNAPSHOT"));
        assertThat(versionFromFileName("helidon-config-2.0.jar"), is("2.0"));
        assertThat(versionFromFileName("helidon-config-2.0-SNAPSHOT.jar"), is("2.0-SNAPSHOT"));
        assertThat(versionFromFileName("helidon-config.jar"), is("0.0.0"));
    }

    @Test
    void testExitOnStartedValue() {
        assertThat(Application.exitOnStartedValue("1.4.1"), is("✅"));
        assertThat(Application.exitOnStartedValue("1.4.2-SNAPSHOT"), is("!"));
        assertThat(Application.exitOnStartedValue("1.4.2"), is("!"));
        assertThat(Application.exitOnStartedValue("2.0-SNAPSHOT"), is("!"));
        assertThat(Application.exitOnStartedValue("2.0"), is("!"));
    }
}
