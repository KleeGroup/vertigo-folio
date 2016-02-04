package io.vertigo.folio.plugins.crawler.ldap;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.impl.crawler.CrawlerPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.folio.plugins.metadata.ldap.LDAPMetaData;
import io.vertigo.lang.Assertion;
import io.vertigo.util.ListBuilder;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.message.BindRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import sun.misc.BASE64Encoder;

/**
 * Created by sbernard on 19/03/2015.
 */
public class LDAPCrawlerPlugin implements CrawlerPlugin {
	private final String myDataSourceId;
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String myDn;

	@Inject
	public LDAPCrawlerPlugin(@Named("dataSourceId") final String dataSourceId, @Named("host") final String host, @Named("port") final int port, @Named("username") final String username, @Named("password") final String password, @Named("dn") final String dn) {
		Assertion.checkArgNotEmpty(dataSourceId);
		Assertion.checkArgNotEmpty(host);
		Assertion.checkNotNull(port);
		Assertion.checkArgNotEmpty(username);
		Assertion.checkNotNull(password);
		Assertion.checkNotNull(dn);
		//-------------
		myDataSourceId = dataSourceId;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		myDn = dn;
	}

	/**
	 * Lit le document avec ses m�ta-donn�es.
	 *
	 * @param documentVersion Identifiant de la version du document
	 * @return Document lu
	 */
	@Override
	public Document readDocument(final DocumentVersion documentVersion) {
		try (final LdapConnection connection = new LdapNetworkConnection(host, port)) {
			final BindRequest bindRequest = new BindRequestImpl()
					.setSimple(true)
					.setDn(new Dn(myDn))
					.setName(username)
					.setCredentials(password);
			connection.bind(bindRequest);
			final Dn ldn = new Dn(documentVersion.getUrl());
			final EntryCursor cursor = connection.search(ldn, "(&(objectclass=person)(objectclass=user))", SearchScope.SUBTREE);
			final Entry entry = cursor.iterator().next();
			return createDocumentFromLdapEntry(entry, documentVersion);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param dataSourceId Id de la datasource
	 * @return si ce plugin g�re cette datasource
	 */
	@Override
	public boolean accept(final String dataSourceId) {
		return myDataSourceId.equals(dataSourceId);
	}

	/**
	 * @param startAtUrl url de d�part
	 * @return Iterator de crawling de documentVersion
	 */
	@Override
	public Iterable<DocumentVersion> crawl(final String startAtUrl) {
		final ListBuilder<Entry> listBuilder = new ListBuilder<>();
		try (final LdapConnection connection = new LdapNetworkConnection(host, port)) {
			final BindRequest bindRequest = new BindRequestImpl()
					.setSimple(true)
					.setDn(new Dn(myDn))
					.setName(username)
					.setCredentials(password);

			connection.bind(bindRequest);
			final Dn dn = new Dn("ou=Utilisateurs,dc=klee,dc=lan,dc=net");
			final EntryCursor cursor = connection.search(dn, "(&(objectclass=person)(objectclass=user))", SearchScope.SUBTREE);
			for (final Entry entry : cursor) {
				listBuilder.add(entry);
			}

			final List list = listBuilder.build();
			return new Iterable<DocumentVersion>() {
				@Override
				public Iterator<DocumentVersion> iterator() {
					return new LDAPEntryIterator(list.iterator(), myDataSourceId);
				}
			};

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Document createDocumentFromLdapEntry(final Entry entry, final DocumentVersion documentVersion) throws Exception {
		final String name = getAttribute(entry, "displayName");
		final String samAccountName = getAttribute(entry, "sAMAccountName");
		final String email = getAttribute(entry, "mail");
		final String company = getAttribute(entry, "company");
		final String department = getAttribute(entry, "department");
		final String firstname = getAttribute(entry, "givenName");
		final String office = getAttribute(entry, "physicalDeliveryOfficeName");
		final String phone = getAttribute(entry, "telephoneNumber");
		final String title = getAttribute(entry, "title");
		final String manager_url = getAttribute(entry, "manager");
		final String thumbnail = encodeImage(entry);

		final MetaDataSet mdc = new MetaDataSetBuilder()
				.addMetaData(LDAPMetaData.NAME, name)
				.addMetaData(LDAPMetaData.SAMACCOUNTNAME, samAccountName)
				.addMetaData(LDAPMetaData.EMAIL, email)
				.addMetaData(LDAPMetaData.COMPANY, company)
				.addMetaData(LDAPMetaData.DEPARTMENT, department)
				.addMetaData(LDAPMetaData.FIRSTNAME, firstname)
				.addMetaData(LDAPMetaData.OFFICE, office)
				.addMetaData(LDAPMetaData.PHONE, phone)
				.addMetaData(LDAPMetaData.TITLE, title)
				.addMetaData(LDAPMetaData.MANAGER_URL, manager_url)
				.addMetaData(LDAPMetaData.THUMBNAIL, thumbnail)
				.build();
		return new DocumentBuilder(documentVersion)
				.withType("LDAP")
				.withName(samAccountName)
				.withSize(entry.size())
				.withSourceMetaDataContainer(mdc)
				.build();
	}

	private static final String getAttribute(final Entry entry, final String attribute) throws Exception {
		if (entry.containsAttribute(attribute)) {
			return entry.get(attribute).getString();
		}
		return "";
	}

	private static final String encodeImage(final Entry entry) throws Exception {
		if (entry.containsAttribute("thumbnailPhoto")) {
			return new BASE64Encoder().encode(entry.get("thumbnailPhoto").getBytes());
		}
		return "";
	}
}
