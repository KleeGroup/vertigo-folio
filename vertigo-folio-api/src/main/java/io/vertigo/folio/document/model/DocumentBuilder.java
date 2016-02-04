package io.vertigo.folio.document.model;

import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Builder;

import java.util.UUID;

/**
 * @author npiedeloup
 * @version $Id: DocumentBuilder.java,v 1.2 2012/02/21 18:01:30 pchretien Exp $
 */
public final class DocumentBuilder implements Builder<Document> {
	//Version
	private Long mySize;

	//ExtractedMetaContent
	private String myName;
	private String myContent;
	private String myType;
	private DocumentCategory myCategory;
	private MetaDataSet mySourceMetaDataContainer;
	//Processed
	private MetaDataSet myEnhancedMetaDataContainer;
	//AddedMetaData
	private MetaDataSet myAddedMetaDataContainer;

	private DocumentStatus myDocumentStatus;

	private final DocumentVersion documentVersion;
	private final Document document;

	public DocumentBuilder(final DocumentVersion documentVersion) {
		Assertion.checkNotNull(documentVersion);
		//-----
		//Constructeur par d�faut.
		this.documentVersion = documentVersion;
		document = null;
		myContent = "";
		myType = "";
		myCategory = new DocumentCategory("", "");
	}

	public DocumentBuilder(final Document document) {
		Assertion.checkNotNull(document);
		Assertion.checkNotNull(document.getDocumentVersion());
		//-----
		documentVersion = document.getDocumentVersion();
		this.document = document;
		myDocumentStatus = document.getDocumentStatus();
	}

	//Version
	public DocumentBuilder withSize(final long size) {
		Assertion.checkArgument(size >= 0, "size must be  >=0");
		//-----
		mySize = size;
		setDirtyFlag();
		return this;
	}

	//ExtractedMetaContent
	public DocumentBuilder withName(final String name) {
		Assertion.checkArgNotEmpty(name);
		//-----
		myName = name;
		return this;
	}

	public DocumentBuilder withContent(final String content) {
		Assertion.checkNotNull(content); //May be empty but not null
		//-----
		myContent = content;
		setDirtyFlag();
		return this;
	}

	public DocumentBuilder withType(final String type) {
		Assertion.checkArgNotEmpty(type);
		//-----
		myType = type;
		setDirtyFlag();
		return this;
	}

	public DocumentBuilder withCategory(final DocumentCategory category) {
		Assertion.checkNotNull(category);
		//-----
		myCategory = category;
		setDirtyFlag();
		return this;
	}

	public DocumentBuilder withSourceMetaDataContainer(final MetaDataSet sourceMetaDataContainer) {
		Assertion.checkNotNull(sourceMetaDataContainer);
		//-----
		mySourceMetaDataContainer = sourceMetaDataContainer;
		setDirtyFlag();
		return this;
	}

	public DocumentBuilder withEnhancedMetaDataContainer(final MetaDataSet enhancedMetaDataContainer) {
		Assertion.checkNotNull(enhancedMetaDataContainer);
		//-----
		myEnhancedMetaDataContainer = enhancedMetaDataContainer;
		setDirtyFlag();
		myDocumentStatus = new DocumentStatus(myDocumentStatus.isIndexed(), myDocumentStatus.isDirty(), true);
		return this;
	}

	public DocumentBuilder withAddedMetaDataContainer(final MetaDataSet addedMetaDataContainer) {
		Assertion.checkNotNull(addedMetaDataContainer);
		//-----
		myAddedMetaDataContainer = addedMetaDataContainer;
		setDirtyFlag();
		return this;
	}

	public DocumentBuilder withIndexed(final boolean indexed) {
		Assertion.checkNotNull(indexed);
		//-----
		setDirtyFlag(false);
		setIndexedFlag();
		return this;
	}

	@Override
	public Document build() {
		if (document == null) {
			//Pour le premier document on commence la r�vision � 0. (Pas de r�vision)
			return new Document(documentVersion, mySize, nextRevision(), myName, myContent, myType, myCategory,
					get(MetaDataSet.EMPTY_META_DATA_SET, mySourceMetaDataContainer),
					get(MetaDataSet.EMPTY_META_DATA_SET, myEnhancedMetaDataContainer),
					get(MetaDataSet.EMPTY_META_DATA_SET, myAddedMetaDataContainer),
					get(new DocumentStatus(false, true, false), myDocumentStatus));
		}

		final long overriddenSize = get(document.getSize(), mySize);
		final String overriddenName = get(document.getName(), myName);
		final String overriddenContent = get(document.getContent(), myContent);
		final String overriddenType = get(document.getType(), myType);
		final DocumentCategory overriddenCategory = get(document.getCategory(), myCategory);
		final MetaDataSet overriddenSourceMetaDataContainer = get(document.getSourceMetaDataContainer(), mySourceMetaDataContainer);
		final MetaDataSet overriddenEnhancedMetaDataContainer = get(document.getEnhancedMetaDataContainer(), myEnhancedMetaDataContainer);
		final MetaDataSet overriddenAddedMetaDataContainer = get(document.getAddedMetaDataContainer(), myAddedMetaDataContainer);
		final DocumentStatus overriddenDocumentStatus = get(document.getDocumentStatus(), myDocumentStatus);

		return new Document(
				documentVersion,
				overriddenSize,
				nextRevision(),
				overriddenName,
				overriddenContent,
				overriddenType,
				overriddenCategory,
				overriddenSourceMetaDataContainer,
				overriddenEnhancedMetaDataContainer,
				overriddenAddedMetaDataContainer,
				overriddenDocumentStatus);
	}

	private static UUID nextRevision() {
		return UUID.randomUUID();
	}

	private static <X> X get(final X firstValue, final X overriddenValue) {
		return overriddenValue != null ? overriddenValue : firstValue;
	}

	private void setDirtyFlag() {
		this.setDirtyFlag(true);
	}

	private void setDirtyFlag(final boolean isDirty) {
		if (myDocumentStatus != null) { // There is already a documentStatus, just change the dirty flag
			myDocumentStatus = new DocumentStatus(myDocumentStatus.isIndexed(), isDirty, myDocumentStatus.isEnhanced());
		} else {
			myDocumentStatus = new DocumentStatus(false, isDirty, false);
		}
	}

	private void setIndexedFlag() {
		if (myDocumentStatus != null) { // There is already a documentStatus, just change the dirty flag
			myDocumentStatus = new DocumentStatus(true, myDocumentStatus.isDirty(), myDocumentStatus.isEnhanced());
		} else {
			myDocumentStatus = new DocumentStatus(true, true, false);
		}
	}
}
