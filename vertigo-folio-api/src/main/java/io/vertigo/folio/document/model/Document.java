package io.vertigo.folio.document.model;

import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.folio.metadata.MetaDataContainerBuilder;
import io.vertigo.lang.Assertion;

import java.io.Serializable;
import java.util.UUID;

/**
 * Document.
 * Un document poss�de n�cessairement une version.
 * @author npiedeloup
 * @version $Id: Document.java,v 1.4 2013/04/25 11:59:53 npiedeloup Exp $
 */
public final class Document implements Serializable {
	//serializable pour etre utilisable en Work
	private static final long serialVersionUID = -8018601431725725697L;

	//Cl� + Version
	private final DocumentVersion documentVersion;

	//Status
	private final DocumentStatus documentStatus;

	//Revision
	private final UUID revision;
	//ExtractedMetaContent
	private final long size;
	private final String name;
	private final String content;
	private final String type;
	private final DocumentCategory category;
	private final MetaDataContainer extractedMetaDataContainer;

	//ProcessedMetaData
	private final MetaDataContainer enhancedMetaDataContainer;

	//UserDefinedMetaData
	private final MetaDataContainer userDefinedMetaDataContainer;

	/**
	 * Constructeur.
	 * @param documentVersion version document (not null)
	 * @param size Taille du document (not null)
	 * @param revision Revision du document
	 * @param name Nom du document (not null)
	 * @param content Contenu extrait du document
	 * @param type Type de document
	 * @param extractedMetaDataContainer Meta-donn�es extraitent du document (not null)
	 * @param enhancedMetaDataContainer Meta-donn�es ajout�es (process) du document (not null)
	 * @param userDefinedMetaDataContainer Meta-donn�es ajout�es (utilisateur) du document (not null)
	 * @param category Category
	 */
	Document(final DocumentVersion documentVersion, final long size, final UUID revision, final String name, final String content, final String type, final DocumentCategory category, final MetaDataContainer extractedMetaDataContainer, final MetaDataContainer enhancedMetaDataContainer, final MetaDataContainer userDefinedMetaDataContainer, final DocumentStatus documentStatus) {
		Assertion.checkNotNull(documentVersion);
		Assertion.checkArgument(size >= 0, "size doit �tre >=0");
		Assertion.checkNotNull(revision);
		Assertion.checkArgNotEmpty(name);
		Assertion.checkNotNull(content); //peut �tre vide
		Assertion.checkNotNull(type); //peut �tre vide
		Assertion.checkNotNull(category);
		Assertion.checkNotNull(extractedMetaDataContainer);
		Assertion.checkNotNull(enhancedMetaDataContainer);
		Assertion.checkNotNull(userDefinedMetaDataContainer);
		Assertion.checkNotNull(documentStatus);
		//--------------------------------------------------------------------
		this.documentVersion = documentVersion;
		this.size = size;
		this.revision = revision;
		this.name = name;
		this.content = content;
		this.type = type;
		this.category = category;
		this.extractedMetaDataContainer = extractedMetaDataContainer;
		//--------------------------------------------------------------------
		this.enhancedMetaDataContainer = enhancedMetaDataContainer;
		this.userDefinedMetaDataContainer = userDefinedMetaDataContainer;
		this.documentStatus = documentStatus;
	}

	//-------------------------------------------------------------------------
	// Cl� + Version
	//-------------------------------------------------------------------------
	public DocumentVersion getDocumentVersion() {
		return documentVersion;
	}

	//Identification des r�visions
	public UUID getRevision() {
		return revision;
	}

	//-------------------------------------------------------------------------
	// ExtractedMetaContent
	//-------------------------------------------------------------------------
	public long getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

	public DocumentCategory getCategory() {
		return category;
	}

	public MetaDataContainer getExtractedMetaDataContainer() {
		return extractedMetaDataContainer;
	}

	//-------------------------------------------------------------------------
	// ProcessedMetaData
	//-------------------------------------------------------------------------
	public MetaDataContainer getEnhancedMetaDataContainer() {
		return enhancedMetaDataContainer;
	}

	//-------------------------------------------------------------------------
	// UserDefinedMetaData
	//-------------------------------------------------------------------------
	public MetaDataContainer getUserDefinedMetaDataContainer() {
		return userDefinedMetaDataContainer;
	}

	//-------------------------------------------------------------------------
	// AggregatedMetaData
	//-------------------------------------------------------------------------
	public MetaDataContainer getMetaDataContainer() {
		//On fabrique � la vol�e le MDC total.
		//@TODO si beaucoup utilis� alors construire au d�marrage.
		//L'ordre est important les MetaDonn�es utilisateurs peuvent donc surcharg�es des Metadonn�es "techniques"
		return new MetaDataContainerBuilder()//
				.withAllMetaDatas(extractedMetaDataContainer)//
				.withAllMetaDatas(enhancedMetaDataContainer)//
				.withAllMetaDatas(userDefinedMetaDataContainer)//
				.build();
	}

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

}
