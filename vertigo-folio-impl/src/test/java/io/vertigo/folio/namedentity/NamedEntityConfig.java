package io.vertigo.folio.namedentity;

import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.folio.impl.namedentity.NamedEntityManagerImpl;
import io.vertigo.folio.plugins.namedentity.recognizer.dbpedia.DbpediaRecognizerPlugin;
import io.vertigo.folio.plugins.namedentity.tokenizer.stanfordNlp.StanfordTokenizerPlugin;

final class NamedEntityConfig {

	static AppConfig build() {
		//@formatter:off
		return new AppConfigBuilder()
				.beginBootModule("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
				.endModule()
				.beginModule("vertigo-folio")
					.addComponent(NamedEntityManager.class, NamedEntityManagerImpl.class)
					.beginPlugin(StanfordTokenizerPlugin.class)
						.addParam("taggerModel", "io/vertigo/folio/plugins/namedentity/tokenizer/stanfordNlp/french.tagger")
					.endPlugin()
					.beginPlugin(DbpediaRecognizerPlugin.class)
						.addParam("proxyHost", "172.20.0.9")
						.addParam("proxyPort", "3128")
					.endPlugin()
				.endModule()
				.build();
		//@formatter:on
	}
}
