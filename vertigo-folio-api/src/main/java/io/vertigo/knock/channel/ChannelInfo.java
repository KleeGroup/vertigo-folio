package io.vertigo.knock.channel;

import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 25/02/2015.
 */
public final class ChannelInfo {
	private final ChannelDefinition channelDefinition;
	private final long documentsCount;

	public ChannelInfo(final ChannelDefinition channelDefinition, final long documentsCount) {
		Assertion.checkNotNull(channelDefinition);
		//-----
		this.channelDefinition = channelDefinition;
		this.documentsCount = documentsCount;
	}

	public ChannelDefinition getChannelDefinition() {
		return channelDefinition;
	}

	public long getDocumentsCount() {
		return documentsCount;
	}
}
