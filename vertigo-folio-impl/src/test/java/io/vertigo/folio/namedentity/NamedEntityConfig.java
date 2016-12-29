package io.vertigo.folio.namedentity;

import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.folio.impl.namedentity.NamedEntityManagerImpl;
import io.vertigo.folio.plugins.namedentity.recognizer.dbpedia.DbpediaRecognizerPlugin;
import io.vertigo.folio.plugins.namedentity.tokenizer.stanfordNlp.StanfordTokenizerPlugin;

final class NamedEntityConfig {

	static AppConfig build() {
		return new AppConfigBuilder()
				.beginBoot()
				.withLocales("fr_FR")
				.addPlugin(ClassPathResourceResolverPlugin.class)
				.endBoot()
				.addModule(new ModuleConfigBuilder("vertigo-folio")
						.addComponent(NamedEntityManager.class, NamedEntityManagerImpl.class)
						.addPlugin(StanfordTokenizerPlugin.class,
								Param.create("taggerModel", "io/vertigo/folio/plugins/namedentity/tokenizer/stanfordNlp/french.tagger"))
						.addPlugin(DbpediaRecognizerPlugin.class,
								Param.create("proxyHost", "172.20.0.9"),
								Param.create("proxyPort", "3128"))
						.build())
				.build();
	}
}
