/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smallrye.config.test.provider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test that configuration is injected into Provider.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
public class ProviderTest extends Arquillian {

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "ProviderTest.jar")
                .addClasses(ProviderTest.class, Email.class, ProviderBean.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new StringAsset(
                        "myEmail=example@smallrye.io"
                ), "microprofile-config.properties")
                .as(JavaArchive.class);
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "ProviderTest.war")
                .addAsLibrary(testJar);
        return war;
    }

    @Inject
    private ProviderBean bean;

    @Test
    public void testDirectInjection() {
        Email email = bean.email;
        Assert.assertNotNull(email);
        assertEquals("example", email.getName());
        assertEquals("smallrye.io", email.getDomain());
    }

    @Test
    public void testProvider() {
        Provider<Email> emailProvider = bean.emailProvider;
        assertNotNull(emailProvider);
        Email email = emailProvider.get();
        assertNotNull(email);
        assertEquals("example", email.getName());
        assertEquals("smallrye.io", email.getDomain());
    }
}
