package io.vertigo.folio.plugins.namedentity.tokenizer.regex;

import io.vertigo.folio.impl.namedentity.TokenizerPlugin;
import io.vertigo.lang.Assertion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

public class RegexTokenizerPlugin implements TokenizerPlugin {
	private final String regex;

	@Inject
	public RegexTokenizerPlugin(@Named("regex") final String regex) {
		Assertion.checkNotNull(regex);
		//----
		this.regex = regex;
	}

	@Override
	public List<String> tokenize(final String text) {
		Assertion.checkNotNull(text);
		//----
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(text);
		final List<String> matches = new ArrayList<>();
		while (matcher.find()) {
			matches.add(matcher.group());
		}
		return matches;
	}

}
