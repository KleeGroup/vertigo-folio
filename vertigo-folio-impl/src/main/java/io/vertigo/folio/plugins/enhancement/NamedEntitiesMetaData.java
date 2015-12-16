package io.vertigo.folio.plugins.enhancement;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 30/12/2014.
 */
public enum NamedEntitiesMetaData implements MetaData {
    NAMED_ENTITIES(MetaDataType.STRING);

    //-----
    private final MetaDataType metaDataType;

    /**
     * Initialise la m�tadonn�e en lui donnant un type
     * @param metaDataType	Type de la m�tadonn�e
     */
    private NamedEntitiesMetaData(final MetaDataType metaDataType) {
        Assertion.checkNotNull(metaDataType);
        //-----
        this.metaDataType = metaDataType;
    }

    /** {@inheritDoc} */
    @Override
    public MetaDataType getType() {
        return metaDataType;
    }
}
