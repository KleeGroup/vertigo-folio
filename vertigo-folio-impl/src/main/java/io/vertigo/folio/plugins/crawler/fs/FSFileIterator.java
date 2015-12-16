package io.vertigo.folio.plugins.crawler.fs;

import io.vertigo.lang.Assertion;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It�rateur des fichiers d'un r�pertoire.
 * @author npiedeloup
 * @version $Id: FSFileIterator.java,v 1.9 2011/10/05 17:17:20 npiedeloup Exp $
 */
final class FSFileIterator implements Iterator<File> {
	private final class OnlyDirectoryFilter implements FileFilter {
		@Override
		public boolean accept(final File file) {
			return file.isDirectory() && checkExcludes(file.getName());
		}
	}

	private final class OnlyFileFilter implements FileFilter {
		@Override
		public boolean accept(final File file) {
			return file.isFile() && checkExcludes(file.getName());
		}
	}

	private final List<Pattern> excludePatterns;
	private int fileCount = 0;
	private final Integer maxFiles;
	private final Stack<File> fileStack = new Stack<>();

	/**
	 * @param directory Repertoire source
	 * @param startAtFile Fichier o� commencer
	 * @param maxFiles Max file ou null si pas de limite
	 */
	FSFileIterator(final File directory, final String startAtFile, final Integer maxFiles, final List<Pattern> excludePatterns) {
		Assertion.checkNotNull(directory);
		Assertion.checkNotNull(startAtFile);
		Assertion.checkNotNull(startAtFile);
		//-----
		this.maxFiles = maxFiles;
		this.excludePatterns = excludePatterns;
		fileStack.add(directory);
		popAllPreviousFile(directory.getAbsolutePath() + startAtFile);
	}

	public boolean checkExcludes(final String fileName) {
		for (final Pattern pattern : excludePatterns) {
			final Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()) {
				return false;
			}
		}
		return true;
	}

	private void popAllPreviousFile(final String lastIndexedFile) {
		//System.out.println("popAllPreviousFile for : " + lastIndexedFile);
		final File first = fileStack.pop();
		File nextFile = first;
		String topFileName = nextFile.getAbsolutePath();
		while (lastIndexedFile.compareTo(topFileName) > 0) {
			if (lastIndexedFile.startsWith(topFileName + File.separator)) {
				//System.out.println("popAllPreviousFile OPEN " + topFileName);
				//on ouvre
				fileStack.addAll(getFiles(nextFile, true));
			} else {
				//System.out.println("popAllPreviousFile ALREADY DONE " + topFileName);
			}
			//il a d�j� �t� fait : on passe au suivant
			if (!fileStack.isEmpty()) {
				nextFile = fileStack.pop();
				topFileName = nextFile.getAbsolutePath();
			} else {
				//si on a vid� la stack, c'est que tout a d�j� �t� fait.
				//System.out.println("popAllPreviousFile ALL DONE " + first.getAbsolutePath());
				return;
			}
		}
		//pas �t� fait (ou le bon) : on le remet dans la stack
		//System.out.println("popAllPreviousFile NEVER DONE " + topFileName);
		fileStack.add(nextFile);
		return;
	}

	private File next;
	private File current;

	/** {@inheritDoc} */
	@Override
	public boolean hasNext() {
		if (next == null) {
			next = doNext();
		}
		return next != null;

	}

	/** {@inheritDoc} */
	@Override
	public File next() {
		if (next == null) {
			throw new NoSuchElementException("Liste vide");
		}
		current = next;
		next = null;
		return current;
	}

	private File doNext() {
		if (maxFiles != null && fileCount > maxFiles || fileStack.isEmpty()) {
			return null;
		}
		final File nextFile = fileStack.pop();
		if (nextFile.isFile()) {
			fileCount++;
			//System.out.println("FSFileInputPipe : " + nextFile.getAbsolutePath() + "(" + nextFile.length() / 1024 + "Ko)");
			return nextFile;
		}
		//directory : on l'ouvre
		fileStack.addAll(getFiles(nextFile, true));
		fileStack.addAll(getFiles(nextFile, false));
		return doNext();//on re-rentre
	}

	/**
	 * @param directory R�pertoire de d�part
	 * @param onlyDirectories si l'on veut les r�pertoires ou les fichiers
	 * @return Liste des fichiers ou des r�pertoires dans l'ordre inverse de l'ordre alphab�tique (pour l'ajout par addAll)
	 */
	private List<File> getFiles(final File directory, final boolean onlyDirectories) {
		Assertion.checkArgument(directory.isDirectory(), "directory doit �tre un r�pertoire");
		//-----
		final List<File> resultFiles;
		final File[] files = directory.listFiles(onlyDirectories ? new OnlyDirectoryFilter() : new OnlyFileFilter());
		if (files != null) {
			resultFiles = Arrays.asList(files);

			Collections.sort(resultFiles, new Comparator<File>() {
				@Override
				public int compare(final File o1, final File o2) {
					return o2.getAbsolutePath().compareTo(o1.getAbsolutePath());
				}
			});
			//System.out.println("FSFileInputPipe OPEN " + directory.getAbsolutePath() + " " + resultFiles.size() + " " + (onlyDirectories ? " directories" : " files"));
		} else {
			//Cas des r�pertoires vide
			resultFiles = Collections.emptyList();
		}
		return resultFiles;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
