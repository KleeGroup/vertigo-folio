package io.vertigo.folio.namedentity;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import io.vertigo.app.AutoCloseableApp;
import io.vertigo.app.config.AppConfig;
import io.vertigo.core.component.di.injector.DIInjector;

/**
 * Test de l'impl�mentation standard.
 *
 * @author pchretien
 * @version $Id: MetaDataManagerTest.java,v 1.5 2014/07/16 13:26:33 pchretien Exp $
 */
public final class NamedEntityManagerTest {
	@Inject
	private NamedEntityManager namedEntityManager;

	private AutoCloseableApp app;

	@Before
	public final void setUp() throws Exception {
		app = new AutoCloseableApp(buildAppConfig());
		DIInjector.injectMembers(this, app.getComponentSpace());
	}

	@Before
	public final void tearDown() throws Exception {
		if (app == null) {
			app.close();
		}
	}

	private AppConfig buildAppConfig() {
		return NamedEntityConfig.build();
	}

	@Test
	public void testNamedentity() {
		final String sentence = "John Fitzgerald Kennedy, the 35th President of the United States, was assassinated at 12:30 p.m. on Friday, November 22, 1963, in Dealey Plaza, Dallas, Texas";
		System.out.println(namedEntityManager.extractNamedEntities(sentence));
		//		Assert.assertEquals("kleegroup", metaDataSet.getValue(MSMetaData.AUTHOR));
	}
}
