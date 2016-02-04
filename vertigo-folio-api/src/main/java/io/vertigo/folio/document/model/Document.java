package io.vertigo.folio.document.model;

import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

import java.io.Serializable;
import java.util.UUID;

/**
 * Document.
 *    |
 *    |--- source   metadata
 *    |--- added    metadata
 *    |--- enhanced metadata
 *    |
 *    |--- content	
 * 
 * @author npiedeloup, pchretien
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

	private final MetaDataSet sourceMetaDataContainer;

	//ProcessedMetaData
	private final MetaDataSet enhancedMetaDataContainer;

	//AddedDefinedMetaData
	private final MetaDataSet addedMetaDataContainer;

	/**
	 * Constructeur.
	 * @param documentVersion version document (not null)
	 * @param size Taille du document (not null)
	 * @param revision Revision du document
	 * @param name Nom du document (not null)
	 * @param content Contenu extrait du document
	 * @param type Type de document
	 * @param sourceMetaDataContainer 	metadata stored in the files (exif, id3...)
	 * @param enhancedMetaDataContainer Meta-donn�es ajout�es (process) du document (not null)
	 * @param addedMetaDataContainer metadata added by a person
	 * @param category Category
	 */
	Document(final DocumentVersion documentVersion, final long size, final UUID revision, final String name, final String content, final String type, final DocumentCategory category, final MetaDataSet sourceMetaDataContainer, final MetaDataSet enhancedMetaDataContainer, final MetaDataSet addedMetaDataContainer, final DocumentStatus documentStatus) {
		Assertion.checkNotNull(documentVersion);
		Assertion.checkArgument(size >= 0, "size doit �tre >=0");
		Assertion.checkNotNull(revision);
		Assertion.checkArgNotEmpty(name);
		Assertion.checkNotNull(content); //peut �tre vide
		Assertion.checkNotNull(type); //peut �tre vide
		Assertion.checkNotNull(category);
		Assertion.checkNotNull(sourceMetaDataContainer);
		Assertion.checkNotNull(enhancedMetaDataContainer);
		Assertion.checkNotNull(addedMetaDataContainer);
		Assertion.checkNotNull(documentStatus);
		//--------------------------------------------------------------------
		this.documentVersion = documentVersion;
		this.size = size;
		this.revision = revision;
		this.name = name;
		this.content = content;
		this.type = type;
		this.category = category;
		this.sourceMetaDataContainer = sourceMetaDataContainer;
		//--------------------------------------------------------------------
		this.enhancedMetaDataContainer = enhancedMetaDataContainer;
		this.addedMetaDataContainer = addedMetaDataContainer;
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

	/**
	 * Returns metadata stored in the source file
	 * examples :
	 *  - exif
	 *  - date
	 *  - GPS data
	 *  - author, producer...
	 *   
	 * @return metadata stored in the source file
	 */
	public MetaDataSet getSourceMetaDataContainer() {
		return sourceMetaDataContainer;
	}

	//-------------------------------------------------------------------------
	// ProcessedMetaData
	//-------------------------------------------------------------------------
	public MetaDataSet getEnhancedMetaDataContainer() {
		return enhancedMetaDataContainer;
	}

	/**
	 * Returns added metadata.
	 * This metadata are added by a person
	 * examples : 
	 *  - keywords
	 *  - comments
	 *  - like
	 *  - tags
	 *  
	 * @return added metadata
	 */
	public MetaDataSet getAddedMetaDataContainer() {
		return addedMetaDataContainer;
	}

	//-------------------------------------------------------------------------
	// AggregatedMetaData
	//-------------------------------------------------------------------------
	public MetaDataSet getMetaDataContainer() {
		//On fabrique � la vol�e le MDC total.
		//@TODO si beaucoup utilis� alors construire au d�marrage.
		//L'ordre est important les MetaDonn�es utilisateurs peuvent donc surcharg�es des Metadonn�es "techniques"
		return new MetaDataSetBuilder()//
				.addAllMetaDatas(sourceMetaDataContainer)//
				.addAllMetaDatas(enhancedMetaDataContainer)//
				.addAllMetaDatas(addedMetaDataContainer)//
				.build();
	}

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}
}
