package io.vertigo.knock.channel;

import io.vertigo.core.spaces.definiton.Definition;
import io.vertigo.core.spaces.definiton.DefinitionPrefix;
import io.vertigo.lang.Assertion;

/**
 * Un channel constitue la définition d'une source d'information.
 */
@DefinitionPrefix("CHN")
public final class ChannelDefinition implements Definition {
	private final String name;
	private final String label; //exemple mes documents
	private final String dataSourceName; //exemple : C:/users/johndoe/
	private final String fontAwesomeIcon;

	/**
	 * Constructeur.
	 *  @param name  Nom du channel
	 * @param label Nom du channel
	 * @param fontAwesomeIcon Icone associée
	 */
	public ChannelDefinition(final String name, final String label, final String dataSourceName, final String fontAwesomeIcon) {
		Assertion.checkArgNotEmpty(name);
		Assertion.checkArgNotEmpty(label);
		Assertion.checkArgNotEmpty(dataSourceName);
		Assertion.checkArgNotEmpty(fontAwesomeIcon);
		//-----
		this.name = name;
		this.label = label;
		this.dataSourceName = dataSourceName;
		this.fontAwesomeIcon = fontAwesomeIcon;
	}

	/**
	 * @return Nom du channel.
	 */
	public String getLabel() {
		return label;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

	public String getFontAwesomeIcon() {
		return fontAwesomeIcon;
	}
}
