package io.vertigo.knock.channel;

import io.vertigo.lang.Component;

import java.util.List;

/**
 * Created by sbernard on 04/02/2015.
 */
public interface ChannelManager extends Component {
	void crawl(final ChannelDefinition channelDefinition);

	void enhance(final ChannelDefinition channelDefinition);

	void index(final ChannelDefinition channelDefinition);

	void crawlAll();

	void enhanceAll();

	void indexAll();

	void drop();

	//	void updateDocumentUserMetadata(final String sourceId, final String documentKey, final Map<UserMetaData, Object> metadata);

	List<ChannelInfo> getChannelInfos();
}
