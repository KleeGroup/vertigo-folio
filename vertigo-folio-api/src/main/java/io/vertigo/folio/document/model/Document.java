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
	private final int size;
	private final String name;
	private final String content;
	private final String type;
	private final DocumentCategory category;

	private final MetaDataSet sourceMetaDataSet;

	//ProcessedMetaData
	private final MetaDataSet enhancedMetaDataSet;

	//AddedDefinedMetaData
	private final MetaDataSet addedMetaDataset;

	/**
	 * Constructeur.
	 * @param documentVersion version document (not null)
	 * @param size Taille du document (not null)
	 * @param revision Revision du document
	 * @param name Nom du document (not null)
	 * @param content Contenu extrait du document
	 * @param type Type de document
	 * @param sourceMetaDataSet 	metadata stored in the files (exif, id3...)
	 * @param enhancedMetaDataSet Meta-donn�es ajout�es (process) du document (not null)
	 * @param addedMetaDataSet metadata added by a person
	 * @param category Category
	 */
	Document(final DocumentVersion documentVersion, final int size, final UUID revision, final String name, final String content, final String type, final DocumentCategory category, final MetaDataSet sourceMetaDataSet, final MetaDataSet enhancedMetaDataSet, final MetaDataSet addedMetaDataSet, final DocumentStatus documentStatus) {
		Assertion.checkNotNull(documentVersion);
		Assertion.checkArgument(size >= 0, "size doit �tre >=0");
		Assertion.checkNotNull(revision);
		Assertion.checkArgNotEmpty(name);
		Assertion.checkNotNull(content); //peut �tre vide
		Assertion.checkNotNull(type); //peut �tre vide
		Assertion.checkNotNull(category);
		Assertion.checkNotNull(sourceMetaDataSet);
		Assertion.checkNotNull(enhancedMetaDataSet);
		Assertion.checkNotNull(addedMetaDataSet);
		Assertion.checkNotNull(documentStatus);
		//--------------------------------------------------------------------
		this.documentVersion = documentVersion;
		this.size = size;
		this.revision = revision;
		this.name = name;
		this.content = content;
		this.type = type;
		this.category = category;
		this.sourceMetaDataSet = sourceMetaDataSet;
		//--------------------------------------------------------------------
		this.enhancedMetaDataSet = enhancedMetaDataSet;
		addedMetaDataset = addedMetaDataSet;
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
	public int getSize() {
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
	public MetaDataSet getSourceMetaDataSet() {
		return sourceMetaDataSet;
	}

	//-------------------------------------------------------------------------
	// ProcessedMetaData
	//-------------------------------------------------------------------------
	public MetaDataSet getEnhancedMetaDataSet() {
		return enhancedMetaDataSet;
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
	public MetaDataSet getAddedMetaDataSet() {
		return addedMetaDataset;
	}

	//-------------------------------------------------------------------------
	// AggregatedMetaData
	//-------------------------------------------------------------------------
	public MetaDataSet getMetaDataSet() {
		//On fabrique � la vol�e le MDC total.
		//@TODO si beaucoup utilis� alors construire au d�marrage.
		//L'ordre est important les MetaDonn�es utilisateurs peuvent donc surcharg�es des Metadonn�es "techniques"
		return new MetaDataSetBuilder()//
				.addMetaDataSet(sourceMetaDataSet)
				.addMetaDataSet(enhancedMetaDataSet)
				.addMetaDataSet(addedMetaDataset)
				.build();
	}

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}
}
