package io.vertigo.folio.metadata;

/**
 * Types des m�ta-donn�es qui peuvent �tre indiqu�es dans les param�tres d'un report.
 * Les M�tadon�es bas�es sur le Dublin Core sont pr�fix�es par DC_
 * http://www-rocq.inria.fr/~vercoust/METADATA/DC-fr.1.1.html
 * http://fr.wikipedia.org/wiki/Dublin_Core
 * 
 * D�finition de la structure d'une m�tadonn�e.
 * Une m�tadonn�e poss�de obligatoirement un domaine, lequel �quivaut � un type enrichi et
 * partag� sur plusieurs objets.
 * Exemple :
 * Domaine Nombre entier positif compris entre 0 et 100,
 * Chaine de 10 caract�re de format AAAA-99999
 * @author npiedeloup, pchretien
 * @version $Id: MetaData.java,v 1.1 2013/07/11 09:13:12 npiedeloup Exp $
 */
public interface MetaData {
	/**
	 * @return Type associ� � la m�tadonn�e (non null)
	 */
	MetaDataType getType();

	/**
	 * Nom (court de la m�tadonn�es).
	 * Attention ce nom n'est pas identifiant.
	 * Il est unique dans un espace de nommage. (ex Dublin Core)
	 */
	String name();

	//Gestion des multi-valu�s ?
	// boolean isSimple();
}
