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
package io.helidon.build.cli.impl;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import io.helidon.build.archetype.engine.ArchetypeCatalog;
import io.helidon.build.cli.impl.InitCommand.Flavor;

import org.junit.jupiter.api.Test;

import static io.helidon.build.cli.impl.ArchetypeBrowser.REMOTE_REPO;
import static io.helidon.build.test.HelidonTestVersions.helidonTestVersion;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Class AppTypeBrowserTest.
 */
public class ArchetypeBrowserTest extends BaseCommandTest {

    /**
     * Test a simple file download from remote repo.
     *
     * @throws Exception If error occurs.
     */
    @Test
    public void testDownload() throws Exception {
        Path file = Path.of("maven-metadata.xml");
        ArchetypeBrowser browser = new ArchetypeBrowser(Flavor.SE, helidonTestVersion());
        browser.downloadArtifact(new URL(REMOTE_REPO + "/io/helidon/build-tools/maven-metadata.xml"), file);
        assertThat(file.toFile().exists(), is(true));
        assertThat(file.toFile().delete(), is(true));
    }

    /**
     * Assertions should be strengthened after we have a release.
     */
    @Test
    public void testMpBrowser() {
        ArchetypeBrowser browser = new ArchetypeBrowser(Flavor.MP, helidonTestVersion());
        List<ArchetypeCatalog.ArchetypeEntry> archetypes = browser.archetypes();
        assertThat(archetypes.size(), is(greaterThanOrEqualTo(0)));
        assertThat(archetypes.stream().map(browser::archetypeJar).count(), is(greaterThanOrEqualTo(0L)));
    }

    /**
     * Assertions should be strengthen after we have a release.
     */
    @Test
    public void testSeBrowser() {
        ArchetypeBrowser browser = new ArchetypeBrowser(Flavor.SE, helidonTestVersion());
        List<ArchetypeCatalog.ArchetypeEntry> archetypes = browser.archetypes();
        assertThat(archetypes.size(), is(greaterThanOrEqualTo(0)));
        assertThat(archetypes.stream().map(browser::archetypeJar).count(), is(greaterThanOrEqualTo(0L)));
    }
}
